# Cars24 SDUI System

This project implements a Server-Driven UI (SDUI) system for the Cars24 Android application. It allows for dynamic UI updates and layout changes without requiring a full app release.

## Screen Selection: Cars24 Home Page
I chose the **Cars24 Home/Landing Page** because it is the most complex and representative screen in the app. It features:
- A search header with location selection and user profile.
- Multiple horizontal carousels (rails) for car categories, loan services, and showrooms.
- Vertical grids for car check services and vehicle management.
- Promotional banners with rich styling.
- A brand footer.

This screen perfectly demonstrates the flexibility of the SDUI system.

## Architecture Overview
The system is built on a **Component-Prop** model:
- **`Screen`**: The top-level container holding a list of components.
- **`Component`**: A generic unit with a `type` and a map of `props`.
- **`ComponentRegistry`**: Maps `type` strings from JSON to native Jetpack Compose functions.
- **`ActionHandler`**: A centralized system to handle interactive intents (navigation, toasts, state changes) defined in JSON.

## Schema Design Rationale
- **Platform Agnostic**: Props are simple key-value pairs or lists of maps, making it easy to implement the same schema on iOS or Web.
- **Decoupled Styling**: Visual properties like `color` are passed in props, allowing the server to control the theme dynamically.
- **Action Strings**: Actions are expressed as strings or simple objects, allowing the client to map them to specific navigation or business logic.

## Versioning Story
- Each JSON payload includes a `schemaVersion`.
- The `JsonLoader` reads this version to decide which parser to use.
- Older app versions can ignore unknown component types thanks to the **Graceful Fallback** mechanism, ensuring the app never crashes when the server introduces new features.

## Unknown Component Fallback
If the server sends a component type that the client doesn't recognize (e.g., a new "LiveAuction" component), the `ComponentRegistry` catches this and renders a generic `Unsupported` component or simply omits it, preventing a crash and allowing the rest of the page to function.

## Setup & Running
1. Open the project in Android Studio.
2. Ensure you have the Material Icons Extended library (handled in `build.gradle.kts`).
3. Run the `app` module on an emulator or physical device.
4. To see the SDUI in action, modify `app/src/main/assets/home.json` and hot-reload or restart the app.
