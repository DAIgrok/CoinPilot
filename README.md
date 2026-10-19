# CoinPilot — projeto Android (código-fonte)

Projeto inicial Android em Kotlin + Jetpack Compose para abrir Shopee/AliExpress, acompanhar tarefas manualmente e enviar um lembrete periódico.

## Abrir
1. Extraia o ZIP.
2. Abra a pasta `CoinPilot-Project` no Android Studio atualizado.
3. Aguarde o Gradle Sync.
4. Conecte o Android com depuração USB ativada ou use um emulador.
5. Clique em Run para instalar no aparelho.
6. Para gerar APK: **Build > Build Bundle(s) / APK(s) > Build APK(s)**.

## O que já existe
- Painel de tarefas para check-in e jogos de ambas as plataformas.
- Botões para abrir Shopee e AliExpress (com fallback para o navegador).
- Registro manual de tarefa pendente ou confirmada pelo usuário.
- Banco local Room para manter os registros.
- Lembrete periódico via WorkManager. O horário não é exato e o Android pode adiar a execução.
- Permissão de notificações no Android 13+.

## Limites importantes
- Este projeto **não coleta moedas automaticamente** e não verifica o saldo real da Shopee/AliExpress.
- “Registrei” grava uma confirmação manual, não uma confirmação do servidor da plataforma.
- Não há ainda notificação de sucesso de coleta, justamente porque o app não tem uma fonte confiável para comprovar a coleta.
- Os identificadores dos aplicativos podem variar por região/versão. Teste os botões no seu aparelho.
- Não contorna CAPTCHA, autenticação, antifraude ou restrições das plataformas.

Este é o código-fonte do projeto; não é um APK pré-compilado nem um build testado neste ambiente.
