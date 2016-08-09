package io.cloudslang.content.jclouds.signature;

import com.hp.oo.sdk.content.annotations.Action;
import com.hp.oo.sdk.content.annotations.Output;
import com.hp.oo.sdk.content.annotations.Param;
import com.hp.oo.sdk.content.annotations.Response;
import com.hp.oo.sdk.content.plugin.ActionMetadata.MatchType;
import com.hp.oo.sdk.content.plugin.ActionMetadata.ResponseType;
import io.cloudslang.content.jclouds.entities.AuthorizationHeader;
import io.cloudslang.content.jclouds.entities.constants.Constants;
import io.cloudslang.content.jclouds.entities.constants.Inputs;
import io.cloudslang.content.jclouds.entities.constants.Outputs;
import io.cloudslang.content.jclouds.services.AmazonSignatureService;
import io.cloudslang.content.jclouds.utils.ExceptionProcessor;
import io.cloudslang.content.jclouds.utils.InputsUtil;
import io.cloudslang.content.jclouds.utils.OutputsUtil;

import java.util.Map;

/**
 * Created by Mihai Tusa.
 * 8/8/2016.
 */
public class ComputeSignatureV4 {
    /**
     * Computes the AWS Signature Version 4 used to authenticate requests by using the authorization header.
     * For this signature type the checksum of the entire payload is computed.
     *
     * @param endpoint    Amazon AWS request endpoint. Ex.: "ec2.amazonaws.com", "s3.amazonaws.com"
     *                    Default: "ec2.amazonaws.com"
     * @param identity    Access Key ID.
     * @param credential  Secret Access Key that correspond to the Access Key ID.
     * @param httpVerb    The request method.
     * @param uri         The request canonical URI.
     * @param encodeUri   Whether to encode or not URI.
     * @param payloadHash The request payload's hash.
     * @param date        List of headers
     * @param queryParams List of query params
     * @param headers     The canonical headers for the request. These headers will be signed.
     * @return A map with strings as keys and strings as values that contains: outcome of the action, returnCode of the
     * operation, or failure message and the exception if there is one
     */
    @Action(name = "Compute Signature V4",
            outputs = {
                    @Output(Outputs.RETURN_CODE),
                    @Output(Outputs.RETURN_RESULT),
                    @Output(Outputs.EXCEPTION),
                    @Output(Constants.AWSParams.SIGNATURE_RESULT),
                    @Output(Constants.AWSParams.AUTHORIZATION_HEADER_RESULT)
            },
            responses = {
                    @Response(text = Outputs.SUCCESS, field = Outputs.RETURN_CODE, value = Outputs.SUCCESS_RETURN_CODE,
                            matchType = MatchType.COMPARE_EQUAL, responseType = ResponseType.RESOLVED),
                    @Response(text = Outputs.FAILURE, field = Outputs.RETURN_CODE, value = Outputs.FAILURE_RETURN_CODE,
                            matchType = MatchType.COMPARE_EQUAL, responseType = ResponseType.ERROR, isOnFail = true)
            })
    public Map<String, String> computeSignature(@Param(value = Inputs.CommonInputs.ENDPOINT) String endpoint,
                                                @Param(value = Inputs.CommonInputs.IDENTITY, required = true) String identity,
                                                @Param(value = Inputs.CommonInputs.CREDENTIAL, required = true, encrypted = true)
                                                        String credential,

                                                @Param(value = Inputs.CustomInputs.HTTP_VERB) String httpVerb,
                                                @Param(value = Inputs.CustomInputs.URI) String uri,
                                                @Param(value = Inputs.CustomInputs.ENCODE_URI) String encodeUri,
                                                @Param(value = Inputs.CustomInputs.PAYLOAD_HASH) String payloadHash,
                                                @Param(value = Inputs.CustomInputs.DATE) String date,
                                                @Param(value = Inputs.CustomInputs.QUERY_PARAMS) String queryParams,
                                                @Param(value = Inputs.CustomInputs.HEADERS) String headers) {

        Map<String, String> queryParamsMap = InputsUtil.getQueryParamsMap(queryParams);
        Map<String, String> headersMap = InputsUtil.getHeadersMap(headers);

        try {
            AuthorizationHeader authorizationHeader = new AmazonSignatureService()
                    .computeSignatureAuthorization(httpVerb, uri, payloadHash, queryParamsMap, headersMap, endpoint,
                            identity, credential, date, Boolean.parseBoolean(encodeUri));

            return OutputsUtil.populateSignatureResultsMap(authorizationHeader.getSignature(), Outputs.SUCCESS_RETURN_CODE,
                    authorizationHeader.getSignature(), authorizationHeader.getAuthorizationHeader());
        } catch (Exception exception) {
            return ExceptionProcessor.getExceptionResult(exception);
        }
    }
}