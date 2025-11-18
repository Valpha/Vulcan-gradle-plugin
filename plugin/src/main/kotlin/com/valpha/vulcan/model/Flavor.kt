package com.valpha.vulcan.model

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryProductFlavor
import com.android.build.api.dsl.ProductFlavor
import org.gradle.api.Action
import org.gradle.api.Named
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import javax.annotation.Nonnull
import javax.annotation.Nullable
import javax.inject.Inject
import kotlin.text.get

abstract class Flavor @Inject constructor(
    objectFactory: ObjectFactory
) : NamedModuleMapping, Named {
    @get:Input
    @get:Optional
    abstract val flavorConfig: Property<Action<LibraryProductFlavor>?>

    fun flavorConfig(action: Action<LibraryProductFlavor>) {
        flavorConfig.set(action)
    }


    override fun toString(): String {
        return "(\"${name}\"${if (flavorConfig.orNull != null) ", extension" else ""})"
    }
}
