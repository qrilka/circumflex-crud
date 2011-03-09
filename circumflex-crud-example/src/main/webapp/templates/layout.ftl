[#ftl]
[#macro page]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
    <link rel="stylesheet"
          type="text/css"
          media="screen"
          href="/css/main.css"/>
    <title>Simple Circumflex Application</title>
  </head>
  <body>
    <div id="header">
        <a href="/">book list</a> |
        <a href="/new">create new book</a>
    </div>
    <div id="outer">
      <div id="content">
        [#nested/]
      </div>
      <div id="footer">
        <span class="copyright">2008-2011</span> ©
        <a class="home" href="http://${headers['Host']!"localhost"}">
          ${headers['Host']!"localhost"}
        </a>
      </div>
    </div>
  </body>
</html>
[/#macro]
[#macro fieldErrors name errors]
  [#if errors[name]??]
  <ul class="field-errors">
    [#list errors[name] as e]
      <li>${e}</li>
    [/#list]
  </ul>
  [/#if]
[/#macro]