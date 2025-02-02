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
 * Adds a new multimap config to a running cluster.
 * If a multimap configuration with the given {@code name} already exists, then
 * the new multimap config is ignored and the existing one is preserved.
 */
public final class DynamicConfigAddMultiMapConfigCodec {
    //hex: 0x1E0100
    public static final int REQUEST_MESSAGE_TYPE = 1966336;
    //hex: 0x1E0101
    public static final int RESPONSE_MESSAGE_TYPE = 1966337;
    private static final int REQUEST_BINARY_FIELD_OFFSET = PARTITION_ID_FIELD_OFFSET + INT_SIZE_IN_BYTES;
    private static final int REQUEST_BACKUP_COUNT_FIELD_OFFSET = REQUEST_BINARY_FIELD_OFFSET + BOOLEAN_SIZE_IN_BYTES;
    private static final int REQUEST_ASYNC_BACKUP_COUNT_FIELD_OFFSET = REQUEST_BACKUP_COUNT_FIELD_OFFSET + INT_SIZE_IN_BYTES;
    private static final int REQUEST_STATISTICS_ENABLED_FIELD_OFFSET = REQUEST_ASYNC_BACKUP_COUNT_FIELD_OFFSET + INT_SIZE_IN_BYTES;
    private static final int REQUEST_MERGE_BATCH_SIZE_FIELD_OFFSET = REQUEST_STATISTICS_ENABLED_FIELD_OFFSET + BOOLEAN_SIZE_IN_BYTES;
    private static final int REQUEST_INITIAL_FRAME_SIZE = REQUEST_MERGE_BATCH_SIZE_FIELD_OFFSET + INT_SIZE_IN_BYTES;
    private static final int RESPONSE_INITIAL_FRAME_SIZE = CORRELATION_ID_FIELD_OFFSET + LONG_SIZE_IN_BYTES;

    private DynamicConfigAddMultiMapConfigCodec() {
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings({"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
    public static class RequestParameters {

        /**
         * multimap configuration name
         */
        public java.lang.String name;

        /**
         * value collection type. Valid values are SET and LIST.
         */
        public java.lang.String collectionType;

        /**
         * entry listener configurations
         */
        public java.util.List<com.hazelcast.client.impl.protocol.task.dynamicconfig.ListenerConfigHolder> listenerConfigs;

        /**
         * {@code true} to store values in {@code BINARY} format or {@code false} to store
         * values in {@code OBJECT} format.
         */
        public boolean binary;

        /**
         * number of synchronous backups
         */
        public int backupCount;

        /**
         * number of asynchronous backups
         */
        public int asyncBackupCount;

        /**
         * set to {@code true} to enable statistics on this multimap configuration
         */
        public boolean statisticsEnabled;

        /**
         * name of an existing configured quorum to be used to determine the minimum number of members
         * required in the cluster for the lock to remain functional. When {@code null}, quorum does not
         * apply to this lock configuration's operations.
         */
        public java.lang.String quorumName;

        /**
         * TODO DOC
         */
        public java.lang.String mergePolicy;

        /**
         * TODO DOC
         */
        public int mergeBatchSize;
    }

    public static ClientMessage encodeRequest(java.lang.String name, java.lang.String collectionType, java.util.Collection<com.hazelcast.client.impl.protocol.task.dynamicconfig.ListenerConfigHolder> listenerConfigs, boolean binary, int backupCount, int asyncBackupCount, boolean statisticsEnabled, java.lang.String quorumName, java.lang.String mergePolicy, int mergeBatchSize) {
        ClientMessage clientMessage = ClientMessage.createForEncode();
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("DynamicConfig.AddMultiMapConfig");
        ClientMessage.Frame initialFrame = new ClientMessage.Frame(new byte[REQUEST_INITIAL_FRAME_SIZE], UNFRAGMENTED_MESSAGE);
        encodeInt(initialFrame.content, TYPE_FIELD_OFFSET, REQUEST_MESSAGE_TYPE);
        encodeBoolean(initialFrame.content, REQUEST_BINARY_FIELD_OFFSET, binary);
        encodeInt(initialFrame.content, REQUEST_BACKUP_COUNT_FIELD_OFFSET, backupCount);
        encodeInt(initialFrame.content, REQUEST_ASYNC_BACKUP_COUNT_FIELD_OFFSET, asyncBackupCount);
        encodeBoolean(initialFrame.content, REQUEST_STATISTICS_ENABLED_FIELD_OFFSET, statisticsEnabled);
        encodeInt(initialFrame.content, REQUEST_MERGE_BATCH_SIZE_FIELD_OFFSET, mergeBatchSize);
        clientMessage.add(initialFrame);
        StringCodec.encode(clientMessage, name);
        StringCodec.encode(clientMessage, collectionType);
        ListMultiFrameCodec.encodeNullable(clientMessage, listenerConfigs, ListenerConfigHolderCodec::encode);
        CodecUtil.encodeNullable(clientMessage, quorumName, StringCodec::encode);
        StringCodec.encode(clientMessage, mergePolicy);
        return clientMessage;
    }

    public static DynamicConfigAddMultiMapConfigCodec.RequestParameters decodeRequest(ClientMessage clientMessage) {
        ListIterator<ClientMessage.Frame> iterator = clientMessage.listIterator();
        RequestParameters request = new RequestParameters();
        ClientMessage.Frame initialFrame = iterator.next();
        request.binary = decodeBoolean(initialFrame.content, REQUEST_BINARY_FIELD_OFFSET);
        request.backupCount = decodeInt(initialFrame.content, REQUEST_BACKUP_COUNT_FIELD_OFFSET);
        request.asyncBackupCount = decodeInt(initialFrame.content, REQUEST_ASYNC_BACKUP_COUNT_FIELD_OFFSET);
        request.statisticsEnabled = decodeBoolean(initialFrame.content, REQUEST_STATISTICS_ENABLED_FIELD_OFFSET);
        request.mergeBatchSize = decodeInt(initialFrame.content, REQUEST_MERGE_BATCH_SIZE_FIELD_OFFSET);
        request.name = StringCodec.decode(iterator);
        request.collectionType = StringCodec.decode(iterator);
        request.listenerConfigs = ListMultiFrameCodec.decodeNullable(iterator, ListenerConfigHolderCodec::decode);
        request.quorumName = CodecUtil.decodeNullable(iterator, StringCodec::decode);
        request.mergePolicy = StringCodec.decode(iterator);
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

    public static DynamicConfigAddMultiMapConfigCodec.ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ListIterator<ClientMessage.Frame> iterator = clientMessage.listIterator();
        ResponseParameters response = new ResponseParameters();
        //empty initial frame
        iterator.next();
        return response;
    }

}
