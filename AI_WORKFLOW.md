# AI Workflow & Collaboration

## Tool Stack
- **IDE**: Android Studio
- **AI**: Cursor / Claude 3.5 Sonnet
- **Context/Rules**: Custom `.cursorrules` and project-specific instructions to enforce Clean Architecture and Jetpack Compose best practices.

## Prompt → Outcome Stories

### 1. Generating the Schema
- **Prompt**: "Design a JSON schema for a car listing app that supports dynamic horizontal rails and vertical grids. The schema should be platform-neutral and support action strings like 'navigate:filter'."
- **Outcome**: The AI suggested a `Component(type, props)` structure. 
- **Refinement**: I rejected the AI's initial idea of nesting styles (margin, padding) inside a separate `style` object for every component. I decided to bake standard dimensions into the `CarsDimens` object on the client to keep the JSON small and performance high, only passing overrides when necessary.

### 2. Registry Fallback
- **Prompt**: "Implement a registry that maps string types to Composables and handles unknown types gracefully."
- **Outcome**: The AI provided a `when` block with an `else` branch.
- **Refinement**: I expanded the `else` branch into a dedicated `Unsupported` component that displays a warning icon in debug builds but remains invisible in production, ensuring a seamless user experience even if the server is "ahead" of the client.

### 3. Action Dispatcher
- **Prompt**: "Create a way to handle actions from JSON like showing a filter screen or opening a toast."
- **Outcome**: The AI suggested using an Interface-based listener.
- **Refinement**: I simplified this into a `lambda (String) -> Unit` passed through the `ScreenRenderer`, which felt more idiomatic in Compose and easier to test.

## AI Failure Story
**Scenario**: Performance Optimization for Grids.
- **Failure**: The AI initially suggested using `LazyVerticalGrid` inside a `verticalScroll` column.
- **Catch**: I caught this immediately as it's a known anti-pattern in Compose that causes crashes or infinite height issues.
- **Fix**: I rewrote the grid components using `Column` with `chunked` rows to ensure they play nicely within the parent scrollable container.

## Verification Strategy
- **Manual Live Edits**: Changing `home.json` values (colors, text) and verifying instant updates.
- **Component Stress Test**: Adding a dummy "Unknown" component to the JSON to verify the fallback logic works without crashing.
- **Static vs SDUI comparison**: Running both activities to ensure visual parity.
