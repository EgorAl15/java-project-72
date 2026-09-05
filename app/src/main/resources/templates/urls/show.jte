@import hexlet.code.model.Url
@import hexlet.code.model.UrlCheck
@import java.util.List

@param Url url
@param List<UrlCheck> checks
@param String flash = null

@template.layout(
    flash = flash,
    content = @`
        <h1 class="mb-6 text-4xl font-light">
            Сайт: ${url.getName()}
        </h1>

        <div class="mb-8 overflow-x-auto">
            <table data-test="url"
                   class="w-full border-collapse text-left">
                <tbody>
                    <tr class="border-b">
                        <td class="px-4 py-3 font-medium">ID</td>
                        <td class="px-4 py-3">${url.getId()}</td>
                    </tr>

                    <tr class="border-b">
                        <td class="px-4 py-3 font-medium">Имя</td>
                        <td class="px-4 py-3">${url.getName()}</td>
                    </tr>

                    <tr class="border-b">
                        <td class="px-4 py-3 font-medium">
                            Дата создания
                        </td>
                        <td class="px-4 py-3">
                            ${url.getCreatedAt().toLocalDateTime().toLocalDate().toString()}
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

        <form
            method="post"
            action="/urls/${url.getId()}/checks"
            class="mb-8"
        >
            <input
                type="submit"
                value="Запустить проверку"
                class="cursor-pointer rounded bg-blue-600 px-5 py-2 text-white hover:bg-blue-700"
            >
        </form>

        <h2 class="mb-4 text-3xl font-light">
            Проверки
        </h2>

        <div class="overflow-x-auto">
            <table data-test="checks"
                   class="w-full border-collapse text-left">
                <thead>
                    <tr class="border-b">
                        <th class="px-4 py-3">ID</th>
                        <th class="px-4 py-3">Код ответа</th>
                        <th class="px-4 py-3">h1</th>
                        <th class="px-4 py-3">title</th>
                        <th class="px-4 py-3">description</th>
                        <th class="px-4 py-3">Дата создания</th>
                    </tr>
                </thead>

                <tbody>
                    @for(var check : checks)
                        <tr class="border-b">
                            <td class="px-4 py-3">
                                ${check.getId()}
                            </td>

                            <td class="px-4 py-3">
                                ${check.getStatusCode()}
                            </td>

                            <td class="px-4 py-3">
                                ${check.getH1()}
                            </td>

                            <td class="px-4 py-3">
                                ${check.getTitle()}
                            </td>

                            <td class="px-4 py-3">
                                ${check.getDescription()}
                            </td>

                            <td class="px-4 py-3">
                                ${check.getCreatedAt().toLocalDateTime().toLocalDate().toString()}
                            </td>
                        </tr>
                    @endfor
                </tbody>
            </table>
        </div>
    `
)