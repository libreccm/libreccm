<!DOCTYPE html>
<html>
    <head>
        <title>Category page</title>
        <link rel="stylesheet" href="${getContextPath()}/theming/ccm/style.css" />
        </head>
    <body>
        <main>
            <h1>${LoginMessages['login.title']}</h1>
            <# if (loginFailed)>
                <div class="alert-error">
                    ${LoginMessages['login.errors.failed']}
                </div>
            </#if>
            <form action="${mvc.url('LoginController#processLogin')}"
                  method="post">
                    <label for="login">${LoginMessages['login.screenname.label']}</label>
                    <input id="login" name="login" required="true" type="text" />
                    
                    <label for="password">
                        ${LoginMessages['login.password.label']}
                    </label>
                    <input id="password" 
                           name="password" 
                           required="true" 
                           type="password" />

                    <button type="submit">
                        ${LoginMessages['login.submit']}
                    </button>
            </form>
        </main>
        <#include "footer.html.ftl">
    </body>
</html>
