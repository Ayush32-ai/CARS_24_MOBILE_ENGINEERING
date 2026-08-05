# SDUI Coverage & Generalization

## Component Registry
The current system supports the following UI patterns:
- **Lists/Rails**: `tileRail`, `loanRail`, `carRail`, `showroomRail` (Horizontal scrolling).
- **Grids**: `serviceGrid`, `vehicleGrid` (Multi-column vertical layouts).
- **Headers**: `homeHeader` with search and interactive tabs.
- **Banners**: `promoBanner` with dynamic coloring and CTAs.
- **Footers**: `brandFooter`.

## Generalization Claim
**"Given a new Cars24 screen, approximately 85% of the UI can be rendered with JSON-only changes."**

### What renders with JSON only:
- Any screen using the existing 9 component types.
- Changing sorting, filtering, or the sequence of sections.
- Updating text, images (via URL/drawable keys), and theme colors.
- Linking to existing actions (Toast, Filter Screen, Navigation).

### What needs new client code:
- **New Layout Patterns**: E.g., a staggered grid or a complex parallax header would require a new component implementation in `ComponentRegistry`.
- **Complex State Management**: If a component needs to maintain complex local state (e.g., a real-time countdown timer), a new specialized component would be needed.
- **Custom Animations**: Highly specific transition animations between components that aren't globally defined.

## Extension Strategy
Adding a new component is a 3-step process:
1. Define the UI in a Composable function.
2. Add the type to `ComponentRegistry`.
3. Map the props from the JSON map.
Using AI assistance, a new standard card or list component can be added in under 15 minutes.
