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
 * Adds a new durable executor configuration to a running cluster.
 * If a durable executor configuration with the given {@code name} already exists, then
 * the new configuration is ignored and the existing one is preserved.
 */
public final class DynamicConfigAddDurableExecutorConfigCodec {
    //hex: 0x1E0A00
    public static final int REQUEST_MESSAGE_TYPE = 1968640;
    //hex: 0x1E0A01
    public static final int RESPONSE_MESSAGE_TYPE = 1968641;
    private static final int REQUEST_POOL_SIZE_FIELD_OFFSET = PARTITION_ID_FIELD_OFFSET + INT_SIZE_IN_BYTES;
    private static final int REQUEST_DURABILITY_FIELD_OFFSET = REQUEST_POOL_SIZE_FIELD_OFFSET + INT_SIZE_IN_BYTES;
    private static final int REQUEST_CAPACITY_FIELD_OFFSET = REQUEST_DURABILITY_FIELD_OFFSET + INT_SIZE_IN_BYTES;
    private static final int REQUEST_INITIAL_FRAME_SIZE = REQUEST_CAPACITY_FIELD_OFFSET + INT_SIZE_IN_BYTES;
    private static final int RESPONSE_INITIAL_FRAME_SIZE = CORRELATION_ID_FIELD_OFFSET + LONG_SIZE_IN_BYTES;

    private DynamicConfigAddDurableExecutorConfigCodec() {
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings({"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
    public static class RequestParameters {

        /**
         * durable executor name
         */
        public java.lang.String name;

        /**
         * executor thread pool size
         */
        public int poolSize;

        /**
         * executor's durability
         */
        public int durability;

        /**
         * capacity of executor tasks per partition
         */
        public int capacity;

        /**
         * name of an existing configured quorum to be used to determine the minimum number of members
         * required in the cluster for the lock to remain functional. When {@code null}, quorum does not
         * apply to this lock configuration's operations.
         */
        public java.lang.String quorumName;
    }

    public static ClientMessage encodeRequest(java.lang.String name, int poolSize, int durability, int capacity, java.lang.String quorumName) {
        ClientMessage clientMessage = ClientMessage.createForEncode();
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("DynamicConfig.AddDurableExecutorConfig");
        ClientMessage.Frame initialFrame = new ClientMessage.Frame(new byte[REQUEST_INITIAL_FRAME_SIZE], UNFRAGMENTED_MESSAGE);
        encodeInt(initialFrame.content, TYPE_FIELD_OFFSET, REQUEST_MESSAGE_TYPE);
        encodeInt(initialFrame.content, REQUEST_POOL_SIZE_FIELD_OFFSET, poolSize);
        encodeInt(initialFrame.content, REQUEST_DURABILITY_FIELD_OFFSET, durability);
        encodeInt(initialFrame.content, REQUEST_CAPACITY_FIELD_OFFSET, capacity);
        clientMessage.add(initialFrame);
        StringCodec.encode(clientMessage, name);
        CodecUtil.encodeNullable(clientMessage, quorumName, StringCodec::encode);
        return clientMessage;
    }

    public static DynamicConfigAddDurableExecutorConfigCodec.RequestParameters decodeRequest(ClientMessage clientMessage) {
        ListIterator<ClientMessage.Frame> iterator = clientMessage.listIterator();
        RequestParameters request = new RequestParameters();
        ClientMessage.Frame initialFrame = iterator.next();
        request.poolSize = decodeInt(initialFrame.content, REQUEST_POOL_SIZE_FIELD_OFFSET);
        request.durability = decodeInt(initialFrame.content, REQUEST_DURABILITY_FIELD_OFFSET);
        request.capacity = decodeInt(initialFrame.content, REQUEST_CAPACITY_FIELD_OFFSET);
        request.name = StringCodec.decode(iterator);
        request.quorumName = CodecUtil.decodeNullable(iterator, StringCodec::decode);
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

    public static DynamicConfigAddDurableExecutorConfigCodec.ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ListIterator<ClientMessage.Frame> iterator = clientMessage.listIterator();
        ResponseParameters response = new ResponseParameters();
        //empty initial frame
        iterator.next();
        return response;
    }

}
