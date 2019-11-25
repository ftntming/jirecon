/*
/*
 * Jirecon, the JItsi REcording COntainer.
 *
 *
 * Copyright @ 2015 Atlassian Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jitsi.jirecon.test;

import org.jitsi.jirecon.IceUdpTransportManager;
import org.jitsi.utils.MediaType;
import org.jitsi.xmpp.extensions.jingle.IceUdpTransportPacketExtension;

import junit.framework.TestCase;

public class TestIceUdpTransportManager
    extends TestCase
{
    public void testEndpoint()
    {
        IceUdpTransportManager mgr = new IceUdpTransportManager();
        
        for (MediaType mediaType : MediaType.values())
        {
            try
            {
                mgr.harvestLocalCandidates(mediaType);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        
        IceUdpTransportPacketExtension pe = null;
        pe = mgr.createTransportPacketExt(MediaType.AUDIO);
        assertNotNull(pe);
        System.out.println(pe.toXML());
    }
}
