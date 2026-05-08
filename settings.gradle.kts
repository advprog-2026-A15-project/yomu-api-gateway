rootProject.name = "api-gateway"

// Include shared-lib as a composite build so the gateway can resolve
// the `id.ac.ui.cs.advprog.yomu:shared-lib` coordinates during development
includeBuild("../shared-lib")