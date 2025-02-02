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

package com.hazelcast.client.impl.protocol.util;

import com.hazelcast.client.impl.MemberImpl;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ClientAddMembershipListenerCodec;
import com.hazelcast.client.impl.protocol.codec.ClientAuthenticationCodec;
import com.hazelcast.client.impl.protocol.codec.MapPutCodec;
import com.hazelcast.cluster.Member;
import com.hazelcast.cluster.MembershipEvent;
import com.hazelcast.internal.networking.HandlerStatus;
import com.hazelcast.internal.serialization.impl.HeapData;
import com.hazelcast.internal.util.counters.SwCounter;
import com.hazelcast.nio.Address;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelJVMTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.version.MemberVersion;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static com.hazelcast.client.impl.protocol.ClientMessage.IS_FINAL_FLAG;
import static com.hazelcast.client.impl.protocol.ClientMessage.UNFRAGMENTED_MESSAGE;
import static com.hazelcast.internal.networking.HandlerStatus.CLEAN;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(HazelcastParallelClassRunner.class)
@Category({QuickTest.class, ParallelJVMTest.class})
public class ClientMessageEncoderDecoderTest extends HazelcastTestSupport {

    @Test
    public void test() {
        ClientMessage message = ClientMessage.createForEncode();
        message.add(new ClientMessage.Frame(new byte[100], UNFRAGMENTED_MESSAGE | IS_FINAL_FLAG));
        message.setMessageType(MapPutCodec.REQUEST_MESSAGE_TYPE);
        AtomicReference<ClientMessage> reference = new AtomicReference<>(message);


        ClientMessageEncoder encoder = new ClientMessageEncoder();
        encoder.src(() -> reference.getAndSet(null));

        ByteBuffer buffer = ByteBuffer.allocate(1000);
        buffer.flip();
        encoder.dst(buffer);

        HandlerStatus result = encoder.onWrite();

        assertEquals(CLEAN, result);

        AtomicReference<ClientMessage> resultingMessage = new AtomicReference<>();
        ClientMessageDecoder decoder = new ClientMessageDecoder(null, resultingMessage::set);
        decoder.setNormalPacketsRead(SwCounter.newSwCounter());

        buffer.position(buffer.limit());

        decoder.src(buffer);
        decoder.onRead();

        assertEquals(message.getMessageType(), resultingMessage.get().getMessageType());
        assertEquals(message.getFrameLength(), resultingMessage.get().getFrameLength());
        assertEquals(message.getHeaderFlags(), resultingMessage.get().getHeaderFlags());
        assertEquals(message.getPartitionId(), resultingMessage.get().getPartitionId());
    }

    @Test
    public void testPut() {
        ClientMessage message =
                MapPutCodec.encodeRequest("map", new HeapData(new byte[100]), new HeapData(new byte[100]), 5, 10);
        AtomicReference<ClientMessage> reference = new AtomicReference<>(message);


        ClientMessageEncoder encoder = new ClientMessageEncoder();
        encoder.src(() -> reference.getAndSet(null));

        ByteBuffer buffer = ByteBuffer.allocate(1000);
        buffer.flip();
        encoder.dst(buffer);

        HandlerStatus result = encoder.onWrite();

        assertEquals(CLEAN, result);

        AtomicReference<ClientMessage> resultingMessage = new AtomicReference<>();
        ClientMessageDecoder decoder = new ClientMessageDecoder(null, resultingMessage::set);
        decoder.setNormalPacketsRead(SwCounter.newSwCounter());

        buffer.position(buffer.limit());

        decoder.src(buffer);
        decoder.onRead();

        assertEquals(message.getMessageType(), resultingMessage.get().getMessageType());
        assertEquals(message.getFrameLength(), resultingMessage.get().getFrameLength());
        assertEquals(message.getHeaderFlags(), resultingMessage.get().getHeaderFlags());
        assertEquals(message.getPartitionId(), resultingMessage.get().getPartitionId());

        MapPutCodec.RequestParameters parameters = MapPutCodec.decodeRequest(resultingMessage.get());

        assertEquals(5, parameters.threadId);
        assertEquals("map", parameters.name);
    }

    @Test
    public void testAuthenticationRequest() {
        Collection<String> labels = new LinkedList<>();
        labels.add("Label");
        String uuid = UUID.randomUUID().toString();
        String ownerUuid = UUID.randomUUID().toString();
        ClientMessage message = ClientAuthenticationCodec.encodeRequest("user", "pass",
                uuid, ownerUuid, true, "JAVA", (byte) 1,
                "1.0", "name", labels, 271, "3.12");
        AtomicReference<ClientMessage> reference = new AtomicReference<>(message);


        ClientMessageEncoder encoder = new ClientMessageEncoder();
        encoder.src(() -> reference.getAndSet(null));

        ByteBuffer buffer = ByteBuffer.allocate(1000);
        buffer.flip();
        encoder.dst(buffer);

        HandlerStatus result = encoder.onWrite();

        assertEquals(CLEAN, result);

        AtomicReference<ClientMessage> resultingMessage = new AtomicReference<>();
        ClientMessageDecoder decoder = new ClientMessageDecoder(null, resultingMessage::set);
        decoder.setNormalPacketsRead(SwCounter.newSwCounter());

        buffer.position(buffer.limit());

        decoder.src(buffer);
        decoder.onRead();

        assertEquals(message.getMessageType(), resultingMessage.get().getMessageType());
        assertEquals(message.getFrameLength(), resultingMessage.get().getFrameLength());
        assertEquals(message.getHeaderFlags(), resultingMessage.get().getHeaderFlags());
        assertEquals(message.getPartitionId(), resultingMessage.get().getPartitionId());

        ClientAuthenticationCodec.RequestParameters parameters = ClientAuthenticationCodec.decodeRequest(resultingMessage.get());

        assertEquals("user", parameters.username);
        assertEquals("pass", parameters.password);
        assertEquals(uuid, parameters.uuid);
        assertEquals(ownerUuid, parameters.ownerUuid);
        assertEquals(true, parameters.isOwnerConnection);
        assertEquals("JAVA", parameters.clientType);
        assertEquals((byte) 1, parameters.serializationVersion);
        assertEquals("1.0", parameters.clientHazelcastVersion);
        assertEquals("name", parameters.clientName);
        assertArrayEquals(labels.toArray(), parameters.labels.toArray());
        assertEquals(271, (int) parameters.partitionCount);
        assertEquals("3.12", parameters.clusterId);
    }

    @Test
    public void testAuthenticationResponse() throws UnknownHostException {
        Collection<Member> members = new LinkedList<>();
        Address address1 = new Address("127.0.0.1", 5702);
        members.add(new MemberImpl(address1, MemberVersion.of("3.12"), UUID.randomUUID().toString()));
        Address address2 = new Address("127.0.0.1", 5703);
        members.add(new MemberImpl(address2, MemberVersion.of("3.12"), UUID.randomUUID().toString()));
        String uuid = UUID.randomUUID().toString();
        String ownerUuid = UUID.randomUUID().toString();


        ClientMessage message = ClientAuthenticationCodec.encodeResponse((byte) 2, new Address("127.0.0.1", 5701),
                uuid, ownerUuid, (byte) 1, "3.12", members, 271, "3.13");
        AtomicReference<ClientMessage> reference = new AtomicReference<>(message);


        ClientMessageEncoder encoder = new ClientMessageEncoder();
        encoder.src(() -> reference.getAndSet(null));

        ByteBuffer buffer = ByteBuffer.allocate(1000);
        buffer.flip();
        encoder.dst(buffer);

        HandlerStatus result = encoder.onWrite();

        assertEquals(CLEAN, result);

        AtomicReference<ClientMessage> resultingMessage = new AtomicReference<>();
        ClientMessageDecoder decoder = new ClientMessageDecoder(null, resultingMessage::set);
        decoder.setNormalPacketsRead(SwCounter.newSwCounter());

        buffer.position(buffer.limit());

        decoder.src(buffer);
        decoder.onRead();

        assertEquals(message.getMessageType(), resultingMessage.get().getMessageType());
        assertEquals(message.getFrameLength(), resultingMessage.get().getFrameLength());
        assertEquals(message.getHeaderFlags(), resultingMessage.get().getHeaderFlags());
        assertEquals(message.getPartitionId(), resultingMessage.get().getPartitionId());

        ClientAuthenticationCodec.ResponseParameters parameters = ClientAuthenticationCodec.decodeResponse(resultingMessage.get());

        assertEquals(2, parameters.status);
        assertEquals(new Address("127.0.0.1", 5701), parameters.address);
        assertEquals(uuid, parameters.uuid);
        assertEquals(ownerUuid, parameters.ownerUuid);
        assertEquals(1, parameters.serializationVersion);
        assertEquals("3.12", parameters.serverHazelcastVersion);
        assertArrayEquals(members.toArray(), parameters.clientUnregisteredMembers.toArray());
        assertEquals(271, parameters.partitionCount);
        assertEquals("3.13", parameters.clusterId);
    }

    class EventHandler extends ClientAddMembershipListenerCodec.AbstractEventHandler {

        Member member;
        int eventType;

        @Override
        public void handleMemberEvent(Member member, int eventType) {
            this.member = member;
            this.eventType = eventType;
        }

        @Override
        public void handleMemberListEvent(Collection<Member> members) {
        }

        @Override
        public void handleMemberAttributeChangeEvent(Member member, Collection<Member> members, String key,
                                                     int operationType, String value) {
        }
    }

    @Test
    public void testEvent() throws UnknownHostException {
        Address address = new Address("127.0.0.1", 5703);
        MemberImpl member = new MemberImpl(address, MemberVersion.of("3.12"), UUID.randomUUID().toString());

        ClientMessage message = ClientAddMembershipListenerCodec.encodeMemberEvent(member, MembershipEvent.MEMBER_ADDED);
        AtomicReference<ClientMessage> reference = new AtomicReference<>(message);

        ClientMessageEncoder encoder = new ClientMessageEncoder();
        encoder.src(() -> reference.getAndSet(null));

        ByteBuffer buffer = ByteBuffer.allocate(1000);
        buffer.flip();
        encoder.dst(buffer);

        HandlerStatus result = encoder.onWrite();

        assertEquals(CLEAN, result);

        AtomicReference<ClientMessage> resultingMessage = new AtomicReference<>();
        ClientMessageDecoder decoder = new ClientMessageDecoder(null, resultingMessage::set);
        decoder.setNormalPacketsRead(SwCounter.newSwCounter());

        buffer.position(buffer.limit());

        decoder.src(buffer);
        decoder.onRead();

        assertEquals(message.getMessageType(), resultingMessage.get().getMessageType());
        assertEquals(message.getFrameLength(), resultingMessage.get().getFrameLength());
        assertEquals(message.getHeaderFlags(), resultingMessage.get().getHeaderFlags());
        assertEquals(message.getPartitionId(), resultingMessage.get().getPartitionId());

        EventHandler eventHandler = new EventHandler();
        eventHandler.handle(resultingMessage.get());

        assertEquals(MembershipEvent.MEMBER_ADDED, eventHandler.eventType);
        assertEquals(member, eventHandler.member);
    }
}
