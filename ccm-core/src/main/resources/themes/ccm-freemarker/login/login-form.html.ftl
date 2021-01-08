<#import "../main.html.ftl" as main>

<@main.ccm_main>
    <h1>${LoginMessages['login.title']}</h1>
            <#if (loginFailed)>
                <div class="alert-error">
                    ${LoginMessages['login.errors.failed']}
                </div>
            </#if>
            <form action="${mvc.uri('LoginController#processLogin')}"
                  class="login"
                  method="post">
                    <div class="form-row">
                        <label for="login">${LoginMessages['login.screenname.label']}</label>
                        <input id="login" name="login" required="true" type="text" />
                    </div>
                    <div class="form-row">
                        <label for="password">
                            ${LoginMessages['login.password.label']}
                        </label>
                        <input id="password" 
                               name="password" 
                               required="true" 
                               type="password" />
                    </div>
                    <input type="hidden" 
                           name="returnUrl" 
                           value="${returnUrl}" />

                    <button type="submit">
                        ${LoginMessages['login.submit']}
                    </button>
            </form>
</@main.ccm_main>
