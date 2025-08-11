androidApplication {
    namespace = "org.example.app"

    dependencies {
        // AndroidX core and appcompat for modern components and compatibility support
        implementation("androidx.core:core-ktx:1.13.1")
        implementation("androidx.appcompat:appcompat:1.7.0")

        // Material Components for Material Design UI (Material 3 included)
        implementation("com.google.android.material:material:1.12.0")

        // RecyclerView, CoordinatorLayout and ConstraintLayout for list and layouts
        implementation("androidx.recyclerview:recyclerview:1.3.2")
        implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
        implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    }

    // Ensure unit tests are discovered by providing JUnit4 in addition to JUnit5
    testing {
        dependencies {
            implementation("junit:junit:4.13.2")
        }
    }
}
