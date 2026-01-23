/********************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.tractusx.edc.tests;

import com.fasterxml.jackson.core.type.TypeReference;
import org.eclipse.edc.iam.decentralizedclaims.spi.validation.TokenValidationAction;
import org.eclipse.edc.iam.verifiablecredentials.spi.model.CredentialSubject;
import org.eclipse.edc.iam.verifiablecredentials.spi.model.Issuer;
import org.eclipse.edc.iam.verifiablecredentials.spi.model.VerifiableCredential;
import org.eclipse.edc.json.JacksonTypeManager;
import org.eclipse.edc.spi.iam.ClaimToken;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.iam.TokenParameters;
import org.eclipse.edc.spi.iam.TokenRepresentation;
import org.eclipse.edc.spi.iam.VerificationContext;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.spi.types.TypeManager;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.eclipse.edc.spi.result.Result.failure;

/**
 * An {@link IdentityService} that will inject the BPN claim in every token.
 * Please only use in testing scenarios!
 */
public class MockVcIdentityService implements IdentityService {
    
    private static final String BUSINESS_PARTNER_NUMBER_CLAIM = "BusinessPartnerNumber";
    private static final String VC_CLAIM = "vc";
    private final String businessPartnerNumber;
    private final String did;
    private final TypeManager typeManager = new JacksonTypeManager();
    private final TokenValidationAction tokenValidationAction;

    public MockVcIdentityService(String businessPartnerNumber, String did, TokenValidationAction tokenValidationAction) {
        this.businessPartnerNumber = businessPartnerNumber;
        this.did = did;
        this.tokenValidationAction = tokenValidationAction;
    }
    
    @Override
    public Result<TokenRepresentation> obtainClientCredentials(String participantContextId, TokenParameters parameters) {
        var credentials = List.of(membershipCredential(), dataExchangeGovernanceCredential());
        var token = Map.of(VC_CLAIM, credentials);

        var tokenRepresentation = TokenRepresentation.Builder.newInstance()
//                .token("{\"vc\":[{\"credentialSubject\":[{\"id\":\"did:web:CONSUMER\",\"holderIdentifier\":\"BPNL0000CONSUMER\"}],\"id\":null,\"type\":[\"VerifiableCredential\",\"MembershipCredential\"],\"issuer\":{\"id\":\"issuer\",\"additionalProperties\":{}},\"issuanceDate\":\"2026-01-22T15:50:57.822662Z\",\"expirationDate\":null,\"credentialStatus\":[],\"description\":null,\"name\":null,\"dataModelVersion\":\"V_1_1\",\"credentialSchema\":[]},{\"credentialSubject\":[{\"id\":\"did:web:CONSUMER\",\"holderIdentifier\":\"BPNL0000CONSUMER\",\"contractVersion\":\"1.0\"}],\"id\":null,\"type\":[\"VerifiableCredential\",\"DataExchangeGovernanceCredential\"],\"issuer\":{\"id\":\"issuer\",\"additionalProperties\":{}},\"issuanceDate\":\"2026-01-22T15:50:57.822673Z\",\"expirationDate\":null,\"credentialStatus\":[],\"description\":null,\"name\":null,\"dataModelVersion\":\"V_1_1\",\"credentialSchema\":[]}]}")
                .token("eyJhbGciOiJFUzI1NksiLCJraWQiOiJkaWQ6d2ViOnBvcnRhbC1iYWNrZW5kLmJldGEuY29maW5pdHkteC5jb206YXBpOmFkbWluaXN0cmF0aW9uOnN0YXRpY2RhdGE6ZGlkOkJQTkwwMDAwMDAwMDBJU1kja2V5cy1hZWU3MDg5MS1hMDhiLTRjOGMtOWZiMi03ZTY5YjI0OGRkZDQiLCJ0eXAiOiJKV1QifQ.eyJhdWQiOiJkaWQ6d2ViOnBvcnRhbC1iYWNrZW5kLmJldGEuY29maW5pdHkteC5jb206YXBpOmFkbWluaXN0cmF0aW9uOnN0YXRpY2RhdGE6ZGlkOkJQTkwwMDAwMDAwMDBJU1kiLCJleHAiOjE3NjkxMDIzMzQsImlhdCI6MTc2OTA5ODczNCwiaXNzIjoiZGlkOndlYjpwb3J0YWwtYmFja2VuZC5iZXRhLmNvZmluaXR5LXguY29tOmFwaTphZG1pbmlzdHJhdGlvbjpzdGF0aWNkYXRhOmRpZDpCUE5MMDAwMDAwMDAwSVNZIiwianRpIjoiMzY0YmM4OTctNGRkNS00YWVmLWJiZjItMDhhOGMyNDc5OTk0Iiwic3ViIjoiZGlkOndlYjpwb3J0YWwtYmFja2VuZC5iZXRhLmNvZmluaXR5LXguY29tOmFwaTphZG1pbmlzdHJhdGlvbjpzdGF0aWNkYXRhOmRpZDpCUE5MMDAwMDAwMDAwSVNZIiwidG9rZW4iOiIyYTdmYzNiZWE1MmYxZWE0MWEwZjk0NTRmNjAxN2MxN2IzNDI3YTgxZWIyNDM0NzIyZDVlOWU2MjkyMmVhZmMzIn0.UfK0bM0E2p72zPKyKK3qkWdhi6YCIOfUu4wFa-427u2ZCUE6fkIuWmG-K3J7HO42tCnEQatmUwuppRI-BQ08oA")
                .build();
        return Result.success(tokenRepresentation);
    }
    
    @Override
    public Result<ClaimToken> verifyJwtToken(String participantContextId, TokenRepresentation tokenRepresentation, VerificationContext verificationContext) {
        var token = tokenRepresentation.getToken();
//        token = token.replace("Bearer ", "").trim();
        tokenRepresentation = tokenRepresentation.toBuilder().token(token).build();

        var claimTokenResult = tokenValidationAction.validate(participantContextId, tokenRepresentation);

        var tokenParsed = typeManager.readValue(token, Map.class);

        if (tokenParsed.containsKey(VC_CLAIM)) {
            var credentials = typeManager.getMapper().convertValue(tokenParsed.get(VC_CLAIM), new TypeReference<List<VerifiableCredential>>(){});
            var claimToken = ClaimToken.Builder.newInstance()
                    .claim(VC_CLAIM, credentials)
                    .build();
            return Result.success(claimToken);
        }
        return Result.failure(format("Expected %s claim, but token did not contain them", VC_CLAIM));
    }
    
    private VerifiableCredential membershipCredential() {
        return VerifiableCredential.Builder.newInstance()
                .type("VerifiableCredential")
                .type("MembershipCredential")
                .credentialSubject(CredentialSubject.Builder.newInstance()
                        .id(did)
                        .claim("holderIdentifier", businessPartnerNumber)
                        .build())
                .issuer(new Issuer("issuer", Map.of()))
                .issuanceDate(Instant.now())
                .build();
    }

    private VerifiableCredential dataExchangeGovernanceCredential() {
        return VerifiableCredential.Builder.newInstance()
                .type("VerifiableCredential")
                .type("DataExchangeGovernanceCredential")
                .credentialSubject(CredentialSubject.Builder.newInstance()
                        .id(did)
                        .claim("holderIdentifier", businessPartnerNumber)
                        .claim("contractVersion", "1.0")
                        .build())
                .issuer(new Issuer("issuer", Map.of()))
                .issuanceDate(Instant.now())
                .build();
    }
}
