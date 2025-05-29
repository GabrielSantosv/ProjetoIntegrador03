# Projeto Integrador 3: Sistema de Controle de Riscos no Ambiente de Trabalho

Este projeto tem como objetivo desenvolver um sistema de monitoramento de riscos no ambiente de trabalho, aplicando conceitos estudados durante o semestre. O sistema será composto por dois aplicativos que irão melhorar a eficiência da gestão de riscos.

## Tecnologias Utilizadas
- **Kotlin** para o aplicativo móvel de registro de riscos
- **Kotlin** para o aplicativo de gerenciamento de riscos
- **Firebase** para banco de dados e autenticação
- **MPAndroidChart** para visualizações gráficas
- **Google Maps** para visualização de riscos no mapa

---

## 📱 APP 1 - Registro de Riscos

O primeiro aplicativo será responsável por registrar e reportar riscos no ambiente de trabalho. Ele permitirá que os usuários façam o envio de riscos diretamente para os gestores.

### 📌 Funcionalidades:
- **Autenticação de usuário**
- **Registro de riscos** com anexos (fotos) e geolocalização
- **Envio de alertas** para a equipe de gestão de riscos

**Tecnologia utilizada:** Kotlin

---

## 🖥️ APP 2 - Gerenciamento de Riscos

O segundo aplicativo será utilizado para visualizar e gerenciar os riscos reportados. Ele permitirá uma análise detalhada dos riscos e auxiliará na tomada de decisões.

### 📌 Funcionalidades:
- **Dashboard Interativo** com:
  - Gráfico de pizza mostrando distribuição por tipo de risco
  - Gráfico de barras exibindo riscos por área
  - Lista de alertas recentes
  - Contador total de riscos
- **Mapa de Riscos**, destacando as principais áreas de risco com base na geolocalização
- **Atualização em tempo real** via Firestore
- **Interface moderna** com Material Design

**Tecnologia utilizada:** Kotlin

---

## 📂 Estrutura do Projeto

```
ControleDeRiscos/
├── app1/                    # Aplicativo de Registro de Riscos
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/       # Código fonte
│   │   │   └── res/        # Recursos
│   │   └── test/           # Testes
│   └── build.gradle.kts    # Configuração do Gradle
│
└── app2/                    # Aplicativo de Gerenciamento
    ├── src/
    │   ├── main/
    │   │   ├── java/       # Código fonte
    │   │   └── res/        # Recursos
    │   └── test/           # Testes
    └── build.gradle.kts    # Configuração do Gradle
```

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
- Android Studio Arctic Fox ou superior
- JDK 11 ou superior
- Dispositivo Android ou emulador com API 21+
- Conta Google para Firebase

### Configuração do Ambiente

1. **Clone o repositório**
   ```bash
   git clone https://github.com/seu-usuario/ProjetoIntegrador3.git
   cd ProjetoIntegrador3
   ```

2. **Configure o Firebase**
   - Crie um projeto no [Firebase Console](https://console.firebase.google.com)
   - Adicione os arquivos `google-services.json` em:
     - `app1/app/google-services.json`
     - `app2/app/google-services.json`

3. **Configure o Google Maps**
   - Obtenha uma API Key no [Google Cloud Console](https://console.cloud.google.com)
   - Adicione a chave no arquivo `local.properties`:
     ```
     MAPS_API_KEY=sua_chave_aqui
     ```

4. **Abra o projeto**
   - Abra o Android Studio
   - Selecione "Open an existing project"
   - Navegue até a pasta do projeto e selecione-a

5. **Sincronize o projeto**
   - Aguarde o Android Studio sincronizar o projeto
   - Resolva quaisquer dependências faltantes

6. **Execute o projeto**
   - Selecione o módulo desejado (app1 ou app2)
   - Clique em "Run" (▶️) ou pressione Shift + F10

### Executando os Testes

```bash
# Testes do APP1
./gradlew :app1:test

# Testes do APP2
./gradlew :app2:test
```

---

## 📌 Contribuição

Sinta-se à vontade para contribuir com o projeto! Para isso:
1. Faça um **fork** do repositório
2. Crie uma **branch** para sua funcionalidade (`feature/nova-funcionalidade`)
3. Envie um **pull request** após testar suas alterações

---

## 📱 Screenshots

### APP2 - Dashboard
- Gráfico de Pizza: Distribuição por tipo de risco
- Gráfico de Barras: Riscos por área
- Lista de Alertas Recentes
- Mapa de Riscos

---

## 🤝 Suporte

Se tiver dúvidas ou sugestões, entre em contato! 🚀

- Abra uma **issue** no GitHub
- Envie um **email** para: seu-email@exemplo.com
- Entre em contato via **LinkedIn**: [seu-linkedin]
