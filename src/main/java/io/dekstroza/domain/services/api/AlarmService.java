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

package io.dekstroza.domain.services.api;

import io.dekstroza.domain.entities.Alarm;
import io.micronaut.data.repository.reactive.RxJavaCrudRepository;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

public interface AlarmService extends RxJavaCrudRepository<Alarm, Integer> {

    Flowable<Alarm> findBySeverity(String severity);

    Single<Alarm> update(Alarm alarm);

    Maybe<Alarm> update(Integer id, Alarm alarm);

}
