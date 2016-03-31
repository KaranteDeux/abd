package abd.phys;

import java.nio.ByteBuffer;

/** Encapsulates a buffer that allows to access a page loaded in memory.
 * A loaded page is obtained from {@link SystemLoadedPagesManager#loadPage(String, int, boolean)} or {@link SystemLoadedPagesManager#loadAsNewPage(String, int, boolean)}
 * The byte buffer should be always accessed through the {@link #getByteBuffer()} method, and <b>should not be assigned</b> to a variable and reused through this variable after releasing the loaded page.
 * This is because the buffer can be wrapped over a memory location that might be reused by another page after this loaded page is released.
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 26 févr. 2016
 */
public interface LoadedPage {
	
	public ByteBuffer getByteBuffer();

}
