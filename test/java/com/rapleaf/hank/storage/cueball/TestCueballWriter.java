/**
 *  Copyright 2011 Rapleaf
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.rapleaf.hank.storage.cueball;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import com.rapleaf.hank.compress.NoCompressionCodec;


public class TestCueballWriter extends AbstractCueballTest {
  public void testWriter() throws Exception {
    ByteArrayOutputStream s = new ByteArrayOutputStream();

    CueballWriter cw = new CueballWriter(s, 10, HASHER, 5, new NoCompressionCodec(), 1);

    cw.write(ByteBuffer.wrap(KEY1), ByteBuffer.wrap(new byte[]{1,2,1,2,1,2}));
    cw.write(ByteBuffer.wrap(KEY2), ByteBuffer.wrap(new byte[]{2,1,2,1,2,1}));
    cw.write(ByteBuffer.wrap(KEY3), ByteBuffer.wrap(new byte[]{(byte) 0x8f,1,2,1,2,1}));
    cw.close();

    byte[] result = s.toByteArray();
    assertEquals(ByteBuffer.wrap(EXPECTED_DATA),
        ByteBuffer.wrap(result));
  }
}
