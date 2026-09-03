@import java.util.List
@import hexlet.code.model.Url

@param List<Url> urls
@param String flash = null

@template.layout(
    flash = flash,
    content = @`
        <h1 class="mb-6 text-4xl font-light">
            Сайты
        </h1>

        <div class="overflow-x-auto">
            <table data-test="urls"
                   class="w-full border-collapse text-left">
                <thead>
                    <tr class="border-b">
                        <th class="px-4 py-3">ID</th>
                        <th class="px-4 py-3">Имя</th>
                        <th class="px-4 py-3">Дата создания</th>
                    </tr>
                </thead>

                <tbody>
                    @for(var url : urls)
                        <tr class="border-b">
                            <td class="px-4 py-3">
                                ${url.getId()}
                            </td>

                            <td class="px-4 py-3">
                                <a href="/urls/${url.getId()}"
                                   class="text-blue-600 hover:underline">
                                    ${url.getName()}
                                </a>
                            </td>

                            <td class="px-4 py-3">
                                ${url.getCreatedAt().toLocalDateTime().toLocalDate().toString()}
                            </td>
                        </tr>
                    @endfor
                </tbody>
            </table>
        </div>
    `
)