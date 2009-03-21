/*                                                                            *\
**           _____  __  _____  __     ____     FieldKit                       **
**          / ___/ / / /____/ / /    /    \    (c) 2009, field                **
**         / ___/ /_/ /____/ / /__  /  /  /    http://www.field.io            **
**        /_/        /____/ /____/ /_____/                                    **
\*                                                                            */
/* created March 18, 2009 */
package field.kit.agent.graph

import field.kit.Logger

/** represents an address-path to a certain node */
class Address {
  var path:String = null
  
  def this(path:String) {
    this()
    this.path = path
  }
  
  def this(node:Node) {
    this()
    def recurse(n:Node, s:String):String =
      if(n.parent!=null) recurse(n.parent, Address.seperator + n.name + s) else s
    this.path = recurse(node, "")
  }
  
  def elements = {
    val p = if(isAbsolute) path.substring(1) else path
    Address.elementsPattern.split(p)
  }
  
  def terminalNodeName = elements.last
  
  def nextNameAfter(name:String) = {
    val i = elements findIndexOf(_ == name)
    if(i == -1 || i >= elements.length)
      null
    else 
      elements(i+1)
  }
  
  def isAbsolute = Address.isAbsolute(path)
  
  def isRelative = Address.isRelative(path)

  override def toString = "Address("+ path +")"
  override def hashCode = path.hashCode
}


/** companion object to class Address */
object Address extends Logger {
  import java.util.regex.Pattern
  import scala.collection.mutable.HashMap
  
  val seperator = '/'
  val parent = "../"
  
  val elementsPattern = Pattern.compile(seperator.toString)
  val parentPattern = Pattern.compile("\\.\\./")
  
  /** maps keys to nodes for faster node lookup */
  val cache = new HashMap[Int,Address]
  
  /** @return the address of the given node */
  def apply(node:Node):Address = apply(node, null)
  
  /** @return an address using the given path and relative to the given node */
  def apply(node:Node, path:String):Address = {
     resolve(node, path) match {
       case path:String => {
         val key = path.hashCode
         cache.get(key) match {
           // re-use address using cache
           case Some(a:Address) => a
           // address is not in cache -> store it now
           case None => 
             val a = new Address(path)
             cache.put(key, a)
             a
         }
       }
       case address:Address => address
     }
  }
  
  def resolve(node:Node, path:String) = {
    // address of the node itself  
    if(path == null) {
      ""
      
//    } else if(node.parent == null) {
//      "/"

    // address is absolute
    } else if(isAbsolute(path)) {
      path
      
    // address is relative, above this node
    } else if(isRelative(path)) {
      
      if(node.parent == null) {
        path
        
      } else {
	      val elements = parentPattern.split(path)
	      
	      // address was just ../ return the parent
	      if(elements.length == 0) {
	        node.parent.address
	        
	      } else {
	        (node.parent.address.path /: elements) ((a:String, b:String) =>
	          if(a == "")
	            b
	          else
	            a + seperator + b
	        )
	      }
      }
    // address is below this node
    } else {
      node.address.path + seperator + path
    }
  }
 
  def isAbsolute(path:String) = path(0) == seperator
  def isRelative(path:String) = !isAbsolute(path)
}