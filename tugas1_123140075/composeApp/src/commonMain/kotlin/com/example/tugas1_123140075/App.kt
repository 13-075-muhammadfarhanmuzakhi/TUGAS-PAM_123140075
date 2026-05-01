@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Halo, FARHAN!",
                style = MaterialTheme.typography.h4
            )

            Text(text = "NIM: 123140075")
            Text(text = "Platform: ${getPlatform().name}")
        }
    }
}