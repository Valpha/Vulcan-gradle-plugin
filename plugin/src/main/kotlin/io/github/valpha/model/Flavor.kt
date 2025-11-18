package io.github.valpha.model

import com.android.build.api.dsl.LibraryProductFlavor
import org.gradle.api.Action
import org.gradle.api.Named
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import javax.inject.Inject

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
