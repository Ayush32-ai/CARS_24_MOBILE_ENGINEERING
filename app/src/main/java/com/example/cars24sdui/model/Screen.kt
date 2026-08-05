package com.example.cars24sdui.model

data class Screen(val components: List<Component>)

data class Component(
    val type: String,
    val props: Map<String, Any?> = emptyMap()
)
