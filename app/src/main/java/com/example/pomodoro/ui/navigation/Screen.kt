sealed class Screen(val route: String) {
    object Timer : Screen("timer")
    object Tasks : Screen("tasks")
    object Settings : Screen("settings")
    object Shop : Screen("shop")
    object Stats : Screen("stats")
    object Rooms : Screen("rooms")  // ← NUEVO
}