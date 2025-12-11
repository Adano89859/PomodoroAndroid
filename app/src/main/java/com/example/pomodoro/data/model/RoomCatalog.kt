package com.example.pomodoro.data.model

object RoomCatalog {

    // JARDÍN - 8 objetos
    val gardenItems = listOf(
        RoomItem(1, "Rosal", "🌹", "Hermosas rosas rojas", 80, RoomType.GARDEN),
        RoomItem(2, "Árbol", "🌳", "Árbol frondoso", 120, RoomType.GARDEN),
        RoomItem(3, "Banco", "🪑", "Banco de jardín", 100, RoomType.GARDEN),
        RoomItem(4, "Fuente", "⛲", "Fuente decorativa", 150, RoomType.GARDEN),
        RoomItem(5, "Macetas", "🪴", "Macetas con flores", 60, RoomType.GARDEN),
        RoomItem(6, "Girasoles", "🌻", "Campo de girasoles", 70, RoomType.GARDEN),
        RoomItem(7, "Farola", "💡", "Farola vintage", 90, RoomType.GARDEN),
        RoomItem(8, "Camino", "🛤️", "Camino de piedras", 50, RoomType.GARDEN)
    )

    // ESCRITORIO - 6 objetos
    val officeItems = listOf(
        RoomItem(11, "Laptop", "💻", "Computadora potente", 150, RoomType.OFFICE),
        RoomItem(12, "Silla", "🪑", "Silla ergonómica", 120, RoomType.OFFICE),
        RoomItem(13, "Lámpara", "🔦", "Lámpara de escritorio", 80, RoomType.OFFICE),
        RoomItem(14, "Planta", "🌿", "Planta decorativa", 60, RoomType.OFFICE),
        RoomItem(15, "Libros", "📚", "Estantería de libros", 100, RoomType.OFFICE),
        RoomItem(16, "Café", "☕", "Taza de café", 50, RoomType.OFFICE)
    )

    // DORMITORIO - 7 objetos
    val bedroomItems = listOf(
        RoomItem(21, "Cama", "🛏️", "Cama cómoda", 150, RoomType.BEDROOM),
        RoomItem(22, "Mesita", "🛋️", "Mesita de noche", 90, RoomType.BEDROOM),
        RoomItem(23, "Lámpara", "💡", "Lámpara de noche", 70, RoomType.BEDROOM),
        RoomItem(24, "Cuadro", "🖼️", "Cuadro decorativo", 80, RoomType.BEDROOM),
        RoomItem(25, "Alfombra", "🧶", "Alfombra suave", 100, RoomType.BEDROOM),
        RoomItem(26, "Ventana", "🪟", "Ventana con cortinas", 110, RoomType.BEDROOM),
        RoomItem(27, "Reloj", "🕐", "Reloj de pared", 60, RoomType.BEDROOM)
    )

    val allItems = gardenItems + officeItems + bedroomItems

    fun getItemById(id: Int): RoomItem? = allItems.find { it.id == id }

    fun getItemsByRoom(roomType: RoomType): List<RoomItem> {
        return when (roomType) {
            RoomType.GARDEN -> gardenItems
            RoomType.OFFICE -> officeItems
            RoomType.BEDROOM -> bedroomItems
        }
    }
}