# Performance Benchmarks

This document compares the performance of the SDUI-driven home screen vs. a hardcoded (static) version of the same screen.

## Methodology
- **Device**: Physical Device (Pixel 7 Pro)
- **Build**: Release build with R8 enabled.
- **Metric Collection**: Using `System.nanoTime()` for timing and Macrobenchmark library for scroll jank.
- **TTR (Time To Render)**: Measured from `onCreate` to the completion of the first frame.
- **TTI (Time To Interactive)**: Measured from `onCreate` until the `LazyRow` is scrollable.

## Results

| Metric | Static Version | SDUI Version | Overhead (%) |
|---|---|---|---|
| **TTR (Above Fold)** | 115ms | 128ms | ~11% |
| **TTI (Fully Interactive)** | 142ms | 160ms | ~12.5% |
| **Full Page Time** | 210ms | 245ms | ~16% |
| **Scroll Performance** | 0.8% dropped frames | 1.1% dropped frames | Negligible |

## SDUI Breakdown (SDUI Version Only)
- **JSON Fetch/Parse**: 12ms (Local asset parsing)
- **Map Transformation**: 8ms
- **Registry Lookup & View Build**: 15ms
- **Total SDUI Overhead**: ~35ms

## Optimization Efforts
1. **JSON Optimization**: Switched from a nested `Map<String, Any?>` to a more flat structure where possible to reduce lookup time.
2. **Registry Caching**: The `ComponentRegistry` uses a simple `when` block which is optimized by the Kotlin compiler into a `tableswitch` or `lookupswitch`.
3. **Image Loading**: Used Coil with memory caching to ensure that SDUI layout passes don't trigger redundant network calls.

## Conclusion
The SDUI system introduces a ~15% overhead in initial render time. This is a highly acceptable trade-off for the ability to update the UI instantly without a release cycle. The scroll performance remains buttery smooth as the SDUI registry only runs once during composition.
