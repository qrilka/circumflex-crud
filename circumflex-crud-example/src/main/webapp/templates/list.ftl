[#ftl]
[#include "layout.ftl"]
[@page]
    [#if flash.contains('message')]
    <div class="message">${flash.message}</div>
    [/#if]
    <ul>
        [#list records as record]
            <li><a href="${prefix}/${record.id}">${record.name?html}</a> - <a href="${prefix}/${record.id}/delete">X</a></li>
        [/#list]
    </ul>
[/@page]
