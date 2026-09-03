@import hexlet.code.model.Url

@param Url url
@param String flash = null

@template.layout(
    flash = flash,
    content = @`
        <h1 class="mb-6 text-4xl font-light">
            Сайт: ${url.getName()}
        </h1>

        <div class="overflow-x-auto">
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
    `
)