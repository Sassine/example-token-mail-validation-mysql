<#import "/spring.ftl" as spring />

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>Ativação de conta usando SpringBoot</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
</head>

<body>
    <h2>Verify your email</h2>

    <@spring.bind "verificationForm" />
    <#if verificationForm?? && noErrors??>
        Foi enviado o link para confirmação da conta para o seu e-mail <strong>${verificationForm.email}</strong><br>
        <#else>
            <form action="/email-verification" method="post">
                <div class="form-group row">
                    <div class="col-8">
                        <div class="input-group">
                            <div class="input-group-prepend">
                                <div class="input-group-text">
                                    <i class="fa fa-at"></i>
                                </div>
                            </div>
                            <@spring.formInput "verificationForm.email" />
                            <@spring.showErrors "<br>" />
                            <br><br>
                            <input type="submit" class="btn btn-primary" value="Submit">
                        </div>
                    </div>
                </div>

            </form>
    </#if>
</body>

</html>
