/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2013-2016 tarent solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.osiam.resources.exception

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import org.osiam.resources.scim.User
import org.springframework.http.HttpStatus
import spock.lang.Specification

class RestExceptionHandlerSpec extends Specification {

    RestExceptionHandler exceptionHandler = new RestExceptionHandler()
    def static IRRELEVANT = 'irrelevant'


    def 'generating a response entity works'() {
        when:
        def result = exceptionHandler.defaultExceptionHandler(new NullPointerException(IRRELEVANT))
        then:
        result.status == HttpStatus.INTERNAL_SERVER_ERROR as String
        result.detail == "An unexpected error occurred"
    }

    def 'status is set to BAD_REQUEST when InvalidConstraintException occurs'() {
        when:
        def result = exceptionHandler.handleInvalidRequests(new InvalidConstraintException(IRRELEVANT))
        then:
        result.detail == "Constraint ${IRRELEVANT} is invalid" as String
        result.status == HttpStatus.BAD_REQUEST as String
    }

    def 'status is set to BAD_REQUEST when InvalidTokenException occurs'() {
        when:
        def result = exceptionHandler.handleInvalidRequests(new InvalidTokenException(IRRELEVANT))
        then:
        result.detail == IRRELEVANT
        result.status == HttpStatus.BAD_REQUEST as String
    }

    def 'status is set to INTERNAL_SERVER_ERROR when OsiamBackendFailureException occurs'() {
        when:
        def result = exceptionHandler.handleBackendFailure()
        then:
        result.detail == "An internal error occurred"
        result.status == HttpStatus.INTERNAL_SERVER_ERROR as String
    }

    def 'status is set to CONFLICT when ResourceExistsException occurs'() {
        when:
        def result = exceptionHandler.handleResourceExists(new ResourceExistsException(IRRELEVANT))
        then:
        result.status == HttpStatus.CONFLICT as String
        result.detail == IRRELEVANT
    }

    def 'status is set to NOT_FOUND when ResourceNotFoundException occurs'() {
        when:
        def result = exceptionHandler.handleResourceNotFoundException(new ResourceNotFoundException(IRRELEVANT))
        then:
        result.status == HttpStatus.NOT_FOUND as String
        result.detail == IRRELEVANT
    }

    def "status is set to NOT_IMPLEMENTED when java.lang.UnsupportedOperationException occurs"() {
        when:
        def result = exceptionHandler.handleUnsupportedOperation(new UnsupportedOperationException(IRRELEVANT))
        then:
        result.status == HttpStatus.NOT_IMPLEMENTED as String
        result.detail == IRRELEVANT
    }

    def "status is set to I_AM_A_TEAPOT when org.osiam.resources.exceptions.SchemaUnknownException occurs"() {
        when:
        def result = exceptionHandler.handleSchemaUnknown(new SchemaUnknownException())
        then:
        result.status == HttpStatus.I_AM_A_TEAPOT as String
    }

    def "should transform json property invalid error message to a more readable response"() {
        given:
        def e = generate_wrong_json_exception('{"schemas":["irrelevant"], "extId":"blubb"}', User)
        when:
        def result = exceptionHandler.handleUnrecognizedProperty(e as UnrecognizedPropertyException)
        then:
        result.status == HttpStatus.CONFLICT as String
        result.detail == 'Unrecognized field "extId"'
    }

    def "should transform json mapping error for List to a more readable response"() {
        given:
        def e = generate_wrong_json_exception('{"ims":"blaa"}', User)
        when:
        def result = exceptionHandler.handleJsonMapping(e as JsonMappingException)
        then:
        result.detail == 'Can not deserialize instance of java.util.ArrayList out of VALUE_STRING'
        result.status == HttpStatus.CONFLICT as String
    }

    Exception generate_wrong_json_exception(String input, Class clazz) {
        try {
            new ObjectMapper().readValue(input, clazz)
        } catch (Exception e) {
            return e
        }
    }
}
