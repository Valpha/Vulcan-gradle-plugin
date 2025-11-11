package com.valpha.vulcan.model

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor
import org.gradle.api.Named
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import javax.annotation.Nonnull
import kotlin.text.get

abstract class Flavor : Named {

    @get:Input
//    @get:Nonnull
    abstract val modulePath: Property<String>


    @get:Input
    @get:Optional
    abstract var ext: ((CommonExtension<*, *, *, *, *, *>).() -> Unit)?

    @get:Input
    @get:Optional
    abstract var extension: (ProductFlavor.() -> Unit)?
    override fun toString(): String {
        return "(\"${modulePath.get()}\"${if (ext != null) ", ext" else ""}${if (extension != null) ", extension" else ""})"
    }
}
