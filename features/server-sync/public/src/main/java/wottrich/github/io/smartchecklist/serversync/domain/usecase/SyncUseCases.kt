package wottrich.github.io.smartchecklist.serversync.domain.usecase

import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.coroutines.UseCase
import wottrich.github.io.smartchecklist.serversync.model.ServerConnectionState

/** Push outbox + pull server changes once. Never user-facing on failure. */
abstract class SyncNowUseCase : KotlinResultUseCase<UseCase.None, UseCase.Empty>()

/** Probe `/health` + `/v1/auth/me`; returns the pairing outcome. */
abstract class TestConnectionUseCase : KotlinResultUseCase<UseCase.None, ServerConnectionState>()

/** Register a new device identity at the given server URL and store the pairing. */
abstract class RegisterDeviceUseCase : KotlinResultUseCase<String, ServerConnectionState>()

/** Clear the stored pairing (never deletes local data). */
abstract class DisconnectServerUseCase : KotlinResultUseCase<UseCase.None, UseCase.Empty>()
