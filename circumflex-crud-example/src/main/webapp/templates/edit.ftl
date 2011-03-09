[#ftl]
[#include "layout.ftl"]
[@page]
[#if flash.contains('message')]
<div class="message">${flash.message}</div>
[/#if]
<form action="${prefix}/${record.id}" method="post">
    <div class="field"><label>Name:</label><input type="text" name="name" value="${record.name!?html}"/></div>
    [@fieldErrors name="name" errors=errors!/]
    <div class="field"><label>Authors:</label><input type="text" name="authors" value="${record.authors!?html}"/></div>
    [@fieldErrors name="authors" errors=errors!/]
    <div class="field"><label>Description:</label>
        <textarea rows="10" cols="40" name="description">${record.description!?html}</textarea></div>
    [@fieldErrors name="description" errors=errors!/]
    <input type="submit"/>
</form>
[/@page]