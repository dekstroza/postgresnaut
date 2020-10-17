/*
 * ------------------------------------------------------------------------------
 *  *******************************************************************************
 *  * COPYRIGHT Ericsson 2012
 *  *
 *  * The copyright to the computer program(s) herein is the property of
 *  * Ericsson Inc. The programs may be used and/or copied only with written
 *  * permission from Ericsson Inc. or in accordance with the terms and
 *  * conditions stipulated in the agreement/contract under which the
 *  * program(s) have been supplied.
 *  *******************************************************************************
 *  *----------------------------------------------------------------------------
 */

package io.dekstroza.rest.controller.api;

import io.dekstroza.domain.entities.Alarm;

import java.util.Collection;
import java.util.Optional;

/**
 * Alarm rest resource api
 */
public interface AlarmController {
    /**
     * Get all alarms from database
     *
     * @return Returns json array of all alarms in the database
     */

    Iterable<Alarm> getAll();

    /**
     * Save single alarm into the database
     *
     * @param alarm
     *            Alarm to be persisted
     * @param id
     *            Integer representing id of this alarm
     * @return Alarm with this id
     */
    Optional<Alarm> findById(Integer id);

    /**
     * Find alarms by severity level
     *
     * @param severity
     *            Requested severity level, can be LOW, MEDIUM or CRITICAL
     * @return All alarms with requested level of severity as json array
     */

    Collection<Alarm> findBySeverity(String severity);

}
