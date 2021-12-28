package com.bdjobs.app.utilities.camera

import java.io.File

abstract class CameraProvider {

     abstract fun initlize()
     abstract fun stop()
     abstract fun record(newFile: File)

     interface OutputCallBack{
         fun videoRecordSuccess(file : File)
         fun videoRecordFailed(message : String)
    }

}