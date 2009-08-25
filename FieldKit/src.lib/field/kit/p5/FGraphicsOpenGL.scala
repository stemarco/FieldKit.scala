/*                                                                            *\
**           _____  __  _____  __     ____     FieldKit                       **
**          / ___/ / / /____/ / /    /    \    (c) 2009, field                **
**         / ___/ /_/ /____/ / /__  /  /  /    http://www.field.io            **
**        /_/        /____/ /____/ /_____/                                    **
\*                                                                            */
/* created March 24, 2009 */
package field.kit.p5

import processing.opengl.PGraphicsOpenGL

/**
 * @author Marcus Wendt
 */
class FGraphicsOpenGL extends PGraphicsOpenGL {
  
  override protected def allocate {
    import javax.media.opengl._
    import processing.core.PConstants._
    
    if (context == null) {
      val capabilities = new GLCapabilities
      
       if (!hints(DISABLE_OPENGL_2X_SMOOTH)) {
        capabilities.setSampleBuffers(true)
        capabilities.setNumSamples(2)
        
      } else if (hints(ENABLE_OPENGL_4X_SMOOTH)) {
        capabilities.setSampleBuffers(true)
        capabilities.setNumSamples(8) // de fault to 8 instead of 4  
      }
      
      val factory = GLDrawableFactory.getFactory
      drawable = factory.getGLDrawable(parent, capabilities, null);
      context = drawable.createContext(null);

      // need to get proper opengl context since will be needed below
      gl = context.getGL
      
      // Flag defaults to be reset on the next trip into beginDraw().
      settingsInited = false
    } else {
      reapplySettings
    }
  }
  
  /*
  private var isCurrent = false
  
  // PGraphicsOpenGL.detainContext leaks memory, so try to call it as rarely as possible
  override def detainContext {
    if(!isCurrent) {
      super.detainContext
      isCurrent = true
    }
  }
  
  override def releaseContext {
  }
  */
}
