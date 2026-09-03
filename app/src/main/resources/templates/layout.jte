@import gg.jte.Content

@param Content content
@param String flash = null

<!doctype html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Анализатор страниц</title>
    <link rel="stylesheet" href="/application.css">
</head>

<body>
<nav class="bg-gray-800">
    <div class="mx-auto flex max-w-6xl items-center px-4 py-4">
        <a href="/" class="text-xl font-medium text-white">
            Анализатор страниц
        </a>

        <a href="/urls"
           class="ml-8 text-gray-300 hover:text-white">
            Сайты
        </a>
    </div>
</nav>

<main class="mx-auto max-w-6xl px-4 py-10">

    @if(flash != null)
        <div role="alert"
             class="mb-6 rounded border border-green-200 bg-green-50 px-4 py-3 text-green-800">
            ${flash}
        </div>
    @endif

    ${content}

</main>
</body>
</html>