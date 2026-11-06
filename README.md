# 🧠 Gustin Emotion Tracker

Um ecossistema de apps para **rastreamento de humor com granularidade emocional** — ajude-se a nomear o que você sente, em menos de 5 segundos, direto do pulso.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.4-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-36%2B-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![Wear OS](https://img.shields.io/badge/Wear%20OS-6-4285F4?logo=android-wear&logoColor=white)](https://developer.android.com/training/wearables)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4)](https://developer.android.com/jetpack/compose)
[![Backend](https://img.shields.io/badge/backend-none-222)](#)

> ⌚ **O relógio é a entrada rápida.** 📱 **O celular é a central de dados.**
> Sem conta, sem nuvem, sem rastreamento — seus dados ficam no seu aparelho.

---

## ✨ Funcionalidades

### ⌚ App do relógio (`:wear`)

| | |
|---|---|
| 🎨 **4 botões coloridos** | Um toque por quadrante emocional — modelo Circunplexo de Afeto |
| 📝 **Emoções específicas** | 5 emoções por quadrante (ex.: *Animado*, *Ansioso*, *Melancólico*…) |
| ⚡ **Registro em <5s** | Quadrante → emoção → check → o app se encerra sozinho |
| 🔔 **Lembretes** | Notificação *"Como você está?"* configurada pelo celular |
| 📴 **Offline-first** | Sem celular por perto? O registro sai quando reconectar |

### 📱 App do celular (`:mobile`)

| | |
|---|---|
| 📜 **Histórico em tempo real** | Data/hora, cor do quadrante e emoção de cada registro |
| 📤 **Exportação CSV** | Últimos 7/30 dias ou intervalo personalizado → WhatsApp, Drive, e-mail… |
| 🖼️ **Gráfico compartilhável** | Imagem com frequência por quadrante + top 5 emoções do período |
| ⏰ **Lembretes configuráveis** | Frequência (1–6h), janela do dia e dias da semana, sincronizados com o relógio |
| 🗑️ **Controle total** | Apagar item individual (com desfazer) ou histórico completo |

---

## 🧭 O modelo

Baseado no **Modelo Circunplexo de Afeto** (Russell, 1980): todo estado emocional pode ser mapeado em dois eixos — **ativação** (energia) e **valência** (agradável/desagradável).

| Quadrante | Cor | Emoções exemplo |
|---|---|---|
| ⚡ Alta energia / Agradável | 🟡 Âmbar | Animado, Eufórico, Motivado |
| ⚡ Alta energia / Desagradável | 🔴 Vermelho | Ansioso, Estressado, Sobrecarregado |
| 🌙 Baixa energia / Agradável | 🟢 Verde | Calmo, Grato, Relaxado |
| 🌙 Baixa energia / Desagradável | 🔵 Índigo | Triste, Cansado, Vazio |

Nomear a emoção específica — em vez de só "bem/mal" — é o que treina granularidade emocional, habilidade associada à regulação emocional.

---

## 🏗️ Arquitetura

```
┌──────────────────┐    Data Layer API (offline-first)    ┌──────────────────┐
│   :wear (Wear OS) │  ─────────── /mood_entry ──────────▶ │  :mobile (Android)│
│                  │                                      │                  │
│  Quadrante ▶     │    /prompt_config (config de         │  MoodListener    │
│  Emoção ▶ Check  │ ◀─────────── lembretes) ──────────── │  Room ▶ Histórico│
└──────────────────┘                                      │  CSV ▶ Gráfico   │
                                                          └──────────────────┘
                                ┌──────────────┐
                                │  :core       │
                                │  Modelo +    │
                                │  Serializer  │
                                └──────────────┘
```

| Módulo | Responsabilidade |
|---|---|
| `:core` | Modelo compartilhado: `MoodEntry`, `Quadrant`, `EmotionCatalog`, serializadores ⇄ `DataMap` |
| `:wear` | Fluxo de 3 telas, envio via `DataClient`, agendador de lembretes (`AlarmManager`) |
| `:mobile` | Room, receptor `WearableListenerService`, histórico, exportação CSV/PNG, DataStore de lembretes |

**Fluxo dos dados:** toque no relógio → Data Layer → listener no celular → Room → `StateFlow` atualiza o histórico em tempo real.

### 🛠️ Stack

- **Kotlin** · **Jetpack Compose** (Material 3 no celular, Wear Material no relógio)
- **Room** para persistência local (`:mobile`)
- **Wearable Data Layer API** para sincronização par-a-par
- **DataStore** para preferências · **FileProvider** + `ACTION_SEND` para compartilhar
- Testes unitários da lógica de agendamento de lembretes

---

## 🚀 Build

```bash
./gradlew :core:assembleDebug :mobile:assembleDebug :wear:assembleDebug
```

**Requisitos:** Android Studio (AGP 9.1+), JDK 11, plataformas Android auto-baixadas pelo Gradle.

```bash
# APKs
mobile/build/outputs/apk/debug/mobile-debug.apk
wear/build/outputs/apk/debug/wear-debug.apk
```

> ⚠️ Ambos os apps compartilham o mesmo `applicationId` — exigência da Data Layer API para entrega par-a-par.

---

## 📁 Estrutura

```
├── core/      # modelo + serialização (UI-free)
├── mobile/    # app Android: dados, histórico, exportação, ajustes
├── wear/      # app Wear OS: entrada rápida + lembretes
└── art/       # ícone da Play Store
```

---

