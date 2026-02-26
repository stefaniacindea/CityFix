package com.example.licenta.ui.citizen



data class ReportItem(
    val name: String,
    val description: String,
    val route: String
)

data class ReportCategory(
    val title: String,
    val iconId: Int,
    val items: List<ReportItem>
)

object ReportCategories {

    const val REPORT_DETAIL_ROUTE = "report_detail"

    val categories = listOf(
        ReportCategory(
            title = "Strazi și Iluminat",
            iconId = 0,
            items = listOf(
                ReportItem("Groapă în asfalt", "Localizează o groapă periculoasă.", REPORT_DETAIL_ROUTE),
                ReportItem("Drum înzăpezit", "Semnalează un drum public necurățat.", REPORT_DETAIL_ROUTE),
                ReportItem("Iluminat stradal defect", "Semnalează un stâlp de iluminat defect.", REPORT_DETAIL_ROUTE)
            )
        ),
        ReportCategory(
            title = "Trafic Auto",
            iconId = 0,
            items = listOf(
                ReportItem("Lipsă semne circulație", "Semnalează o zonă cu semne lipsă.", REPORT_DETAIL_ROUTE),
                ReportItem("Semafor defect", "Semnalează un semafor care nu funcționează.", REPORT_DETAIL_ROUTE),
                ReportItem("Vehicul abandonat", "Localizează un vehicul abandonat pe domeniul public.", REPORT_DETAIL_ROUTE)
            )
        ),
        ReportCategory(
            title = "Parcuri și Spații Verzi",
            iconId = 0,
            items = listOf(
                ReportItem("Spațiu verde neîngrijit", "Semnalează iarbă sau vegetație neîngrijită.", REPORT_DETAIL_ROUTE),
                ReportItem("Loc de joacă deteriorat", "Semnalează echipament stricat în locurile de joacă.", REPORT_DETAIL_ROUTE),
                ReportItem("Copac cu risc de prăbușire", "Semnalează un copac uscat sau periculos.", REPORT_DETAIL_ROUTE)
            )
        )
    )
}