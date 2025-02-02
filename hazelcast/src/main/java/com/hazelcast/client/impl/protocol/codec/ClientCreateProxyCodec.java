/*
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.builtin.*;

import java.util.ListIterator;

import static com.hazelcast.client.impl.protocol.ClientMessage.*;
import static com.hazelcast.client.impl.protocol.codec.builtin.FixedSizeTypesCodec.*;

/**
 * TODO DOC
 */
public final class ClientCreateProxyCodec {
    //hex: 0x000500
    public static final int REQUEST_MESSAGE_TYPE = 1280;
    //hex: 0x000501
    public static final int RESPONSE_MESSAGE_TYPE = 1281;
    private static final int REQUEST_INITIAL_FRAME_SIZE = PARTITION_ID_FIELD_OFFSET + INT_SIZE_IN_BYTES;
    private static final int RESPONSE_INITIAL_FRAME_SIZE = CORRELATION_ID_FIELD_OFFSET + LONG_SIZE_IN_BYTES;

    private ClientCreateProxyCodec() {
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings({"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
    public static class RequestParameters {

        /**
         * The distributed object name for which the proxy is being created for.
         */
        public java.lang.String name;

        /**
         * The name of the service. Possible service names are:
         * "hz:impl:listService"
         * "hz:impl:queueService"
         * "hz:impl:setService"
         * "hz:impl:atomicLongService"
         * "hz:impl:atomicReferenceService"
         * "hz:impl:countDownLatchService"
         * "hz:impl:idGeneratorService"
         * "hz:impl:semaphoreService"
         * "hz:impl:executorService"
         * "hz:impl:mapService"
         * "hz:impl:multiMapService"
         * "hz:impl:quorumService"
         * "hz:impl:replicatedMapService"
         * "hz:impl:ringbufferService"
         * "hz:core:proxyService"
         * "hz:impl:reliableTopicService"
         * "hz:impl:topicService"
         * "hz:core:txManagerService"
         * "hz:impl:xaService"
         */
        public java.lang.String serviceName;

        /**
         * TODO DOC
         */
        public com.hazelcast.nio.Address target;
    }

    public static ClientMessage encodeRequest(java.lang.String name, java.lang.String serviceName, com.hazelcast.nio.Address target) {
        ClientMessage clientMessage = ClientMessage.createForEncode();
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Client.CreateProxy");
        ClientMessage.Frame initialFrame = new ClientMessage.Frame(new byte[REQUEST_INITIAL_FRAME_SIZE], UNFRAGMENTED_MESSAGE);
        encodeInt(initialFrame.content, TYPE_FIELD_OFFSET, REQUEST_MESSAGE_TYPE);
        clientMessage.add(initialFrame);
        StringCodec.encode(clientMessage, name);
        StringCodec.encode(clientMessage, serviceName);
        AddressCodec.encode(clientMessage, target);
        return clientMessage;
    }

    public static ClientCreateProxyCodec.RequestParameters decodeRequest(ClientMessage clientMessage) {
        ListIterator<ClientMessage.Frame> iterator = clientMessage.listIterator();
        RequestParameters request = new RequestParameters();
        //empty initial frame
        iterator.next();
        request.name = StringCodec.decode(iterator);
        request.serviceName = StringCodec.decode(iterator);
        request.target = AddressCodec.decode(iterator);
        return request;
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings({"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
    public static class ResponseParameters {
    }

    public static ClientMessage encodeResponse() {
        ClientMessage clientMessage = ClientMessage.createForEncode();
        ClientMessage.Frame initialFrame = new ClientMessage.Frame(new byte[RESPONSE_INITIAL_FRAME_SIZE], UNFRAGMENTED_MESSAGE);
        encodeInt(initialFrame.content, TYPE_FIELD_OFFSET, RESPONSE_MESSAGE_TYPE);
        clientMessage.add(initialFrame);

        return clientMessage;
    }

    public static ClientCreateProxyCodec.ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ListIterator<ClientMessage.Frame> iterator = clientMessage.listIterator();
        ResponseParameters response = new ResponseParameters();
        //empty initial frame
        iterator.next();
        return response;
    }

}
