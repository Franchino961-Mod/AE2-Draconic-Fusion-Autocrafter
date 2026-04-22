package com.ae2draconicfusion.autocrafter.fusion;

/**
 * Possible outcomes of a fusion routing attempt.
 */
public enum FusionRoutingResult {
    /** Item was successfully placed in the core or an injector. */
    SUCCESS,
    /** No valid Fusion Crafting Core was found at the target position. */
    INVALID_CORE,
    /** Core is valid but has no connected injectors. */
    NO_INJECTORS_FOUND,
    /** Not enough empty injectors to accommodate all items. */
    INJECTORS_INSUFFICIENT,
    /** Target slots (core or injectors) are already occupied. */
    TARGET_SLOTS_OCCUPIED,
    /** Routing failed temporarily (e.g. core busy); AE2 should retry later. */
    TEMPORARY_FAILURE
}
