package de.noneless.lobby.world;

import java.util.UUID;

/**
 * Immutable snapshot of an active world move job for persistence.
 */
public record WorldMoveState(
        String worldName,
        int offset,
        int minChunkX,
        int maxChunkX,
        int minChunkZ,
        int maxChunkZ,
        int nextChunkX,
        int nextChunkZ,
        long processedChunks,
        long totalChunks,
        long startedAtMillis,
        String initiatorName,
        UUID initiatorUuid,
        boolean initiatedByPlayer
) {
}
