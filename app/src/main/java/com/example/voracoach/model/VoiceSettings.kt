package com.example.voracoach.model
// Cómo habla el coach: tono general
enum class CoachingStyle { SUPPORTIVE, BALANCED, DIRECT }

// Qué tan largas son las respuestas
enum class ResponseStyle { CONCISE, BALANCED, DETAILED }

// Preferencias de "Voice & AI Settings". Son de demostración: no hay audio ni IA reales,
// pero los valores iniciales son los que muestra Figma.
data class VoiceSettings(
    val coachingStyle: CoachingStyle = CoachingStyle.BALANCED,
    val speakResponses: Boolean = true,
    val autoPlayCoaching: Boolean = true,
    val allowInterruptions: Boolean = true,
    val responseStyle: ResponseStyle = ResponseStyle.BALANCED,
    val voiceFeedback: Boolean = true,
    val hapticFeedback: Boolean = true
)