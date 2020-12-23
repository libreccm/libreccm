<!DOCTYPE html>
<html>
    <head>
        <title>Category page</title>
        <link rel="stylesheet" href="${getContextPath()}/theming/ccm/style.css" />
        </head>
    <body>
        <main>
            <h1>${LoginMessages['login.recover_password.title']}</h1>
            <# if (failedToSendRecoverMessage)>
                <div class="alert-error">
                    ${LoginMessages['login.errors.failedToSendRecoverMessage']}
                </div>
            </#if>
            <form action="${mvc.url('LoginController#recoverPassword')}"
                  method="post">
                    <label for="email">${LoginMessages['login.email.label']}</label>
                    <input id="email" name="email" required="true" type="text" />

                    <button type="submit">
                        ${LoginMessages['login.recover_password.submit']}
                    </button>
            </form>
        </main>
        <#include "footer.html.ftl">
    </body>
</html>
