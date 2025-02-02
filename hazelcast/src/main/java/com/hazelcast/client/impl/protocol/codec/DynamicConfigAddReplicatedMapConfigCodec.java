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
 * Adds a new replicated map configuration to a running cluster.
 * If a replicated map configuration with the given {@code name} already exists, then
 * the new configuration is ignored and the existing one is preserved.
 */
public final class DynamicConfigAddReplicatedMapConfigCodec {
    //hex: 0x1E0700
    public static final int REQUEST_MESSAGE_TYPE = 1967872;
    //hex: 0x1E0701
    public static final int RESPONSE_MESSAGE_TYPE = 1967873;
    private static final int REQUEST_ASYNC_FILLUP_FIELD_OFFSET = PARTITION_ID_FIELD_OFFSET + INT_SIZE_IN_BYTES;
    private static final int REQUEST_STATISTICS_ENABLED_FIELD_OFFSET = REQUEST_ASYNC_FILLUP_FIELD_OFFSET + BOOLEAN_SIZE_IN_BYTES;
    private static final int REQUEST_MERGE_BATCH_SIZE_FIELD_OFFSET = REQUEST_STATISTICS_ENABLED_FIELD_OFFSET + BOOLEAN_SIZE_IN_BYTES;
    private static final int REQUEST_INITIAL_FRAME_SIZE = REQUEST_MERGE_BATCH_SIZE_FIELD_OFFSET + INT_SIZE_IN_BYTES;
    private static final int RESPONSE_INITIAL_FRAME_SIZE = CORRELATION_ID_FIELD_OFFSET + LONG_SIZE_IN_BYTES;

    private DynamicConfigAddReplicatedMapConfigCodec() {
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings({"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
    public static class RequestParameters {

        /**
         * name of the replicated map configuration
         */
        public java.lang.String name;

        /**
         * data type used to store entries. Valid values are {@code "BINARY"}, {@code "OBJECT"}
         * and {@code "NATIVE"}.
         */
        public java.lang.String inMemoryFormat;

        /**
         * {@code true} to make the replicated map available for reads before initial replication
         * is completed, {@code false} otherwise.
         */
        public boolean asyncFillup;

        /**
         * {@code true} to enable gathering of statistics, otherwise {@code false}
         */
        public boolean statisticsEnabled;

        /**
         * class name of a class implementing
         * {@code com.hazelcast.replicatedmap.merge.ReplicatedMapMergePolicy} to merge entries
         * while recovering from a split brain
         */
        public java.lang.String mergePolicy;

        /**
         * entry listener configurations
         */
        public java.util.List<com.hazelcast.client.impl.protocol.task.dynamicconfig.ListenerConfigHolder> listenerConfigs;

        /**
         * name of an existing configured quorum to be used to determine the minimum number of members
         * required in the cluster for the lock to remain functional. When {@code null}, quorum does not
         * apply to this lock configuration's operations.
         */
        public java.lang.String quorumName;

        /**
         * TODO DOC
         */
        public int mergeBatchSize;
    }

    public static ClientMessage encodeRequest(java.lang.String name, java.lang.String inMemoryFormat, boolean asyncFillup, boolean statisticsEnabled, java.lang.String mergePolicy, java.util.Collection<com.hazelcast.client.impl.protocol.task.dynamicconfig.ListenerConfigHolder> listenerConfigs, java.lang.String quorumName, int mergeBatchSize) {
        ClientMessage clientMessage = ClientMessage.createForEncode();
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("DynamicConfig.AddReplicatedMapConfig");
        ClientMessage.Frame initialFrame = new ClientMessage.Frame(new byte[REQUEST_INITIAL_FRAME_SIZE], UNFRAGMENTED_MESSAGE);
        encodeInt(initialFrame.content, TYPE_FIELD_OFFSET, REQUEST_MESSAGE_TYPE);
        encodeBoolean(initialFrame.content, REQUEST_ASYNC_FILLUP_FIELD_OFFSET, asyncFillup);
        encodeBoolean(initialFrame.content, REQUEST_STATISTICS_ENABLED_FIELD_OFFSET, statisticsEnabled);
        encodeInt(initialFrame.content, REQUEST_MERGE_BATCH_SIZE_FIELD_OFFSET, mergeBatchSize);
        clientMessage.add(initialFrame);
        StringCodec.encode(clientMessage, name);
        StringCodec.encode(clientMessage, inMemoryFormat);
        StringCodec.encode(clientMessage, mergePolicy);
        ListMultiFrameCodec.encodeNullable(clientMessage, listenerConfigs, ListenerConfigHolderCodec::encode);
        CodecUtil.encodeNullable(clientMessage, quorumName, StringCodec::encode);
        return clientMessage;
    }

    public static DynamicConfigAddReplicatedMapConfigCodec.RequestParameters decodeRequest(ClientMessage clientMessage) {
        ListIterator<ClientMessage.Frame> iterator = clientMessage.listIterator();
        RequestParameters request = new RequestParameters();
        ClientMessage.Frame initialFrame = iterator.next();
        request.asyncFillup = decodeBoolean(initialFrame.content, REQUEST_ASYNC_FILLUP_FIELD_OFFSET);
        request.statisticsEnabled = decodeBoolean(initialFrame.content, REQUEST_STATISTICS_ENABLED_FIELD_OFFSET);
        request.mergeBatchSize = decodeInt(initialFrame.content, REQUEST_MERGE_BATCH_SIZE_FIELD_OFFSET);
        request.name = StringCodec.decode(iterator);
        request.inMemoryFormat = StringCodec.decode(iterator);
        request.mergePolicy = StringCodec.decode(iterator);
        request.listenerConfigs = ListMultiFrameCodec.decodeNullable(iterator, ListenerConfigHolderCodec::decode);
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

    public static DynamicConfigAddReplicatedMapConfigCodec.ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ListIterator<ClientMessage.Frame> iterator = clientMessage.listIterator();
        ResponseParameters response = new ResponseParameters();
        //empty initial frame
        iterator.next();
        return response;
    }

}
