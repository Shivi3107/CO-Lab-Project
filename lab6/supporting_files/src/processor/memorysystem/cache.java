package processor.memorysystem;

import configuration.Configuration;
import generic.Element;
import generic.Event;
import generic.Event.EventType;
import generic.MemoryReadEvent;
import generic.MemoryResponseEvent;
import generic.MemoryWriteEvent;
import generic.Simulator;
import processor.Clock;
import processor.Processor;
import processor.pipeline.MemoryAccess;

public class cache implements Element
{
	Processor containingProcessor;
	public int latency, size;
	int Miss_address;
	Element Miss;
	int read_value, write_value;
	cacheLine[] caches;
	
	public cache(Processor containingProcessor, int l, int n)
	{
		this.containingProcessor = containingProcessor;
		latency = l;
		size = n / 4;
		caches = new cacheLine[size];
		for(int i = 0; i < size; i++)
		{
			caches[i] = new cacheLine();
		}
	}
	
	public void cacheRead(int addr, Element requesting_element)
	{
		int cache_addr;
		if((int)(Math.log(size)/Math.log(2)) == 0)
		{
			cache_addr = 0;
		}
		else
		{
			cache_addr = addr << (32 - (int)(Math.log(size)/Math.log(2)));
			cache_addr = addr >>> (32 - (int)(Math.log(size)/Math.log(2)));
		}	
		
		if(caches[cache_addr].tag == addr)
		{
			Simulator.getEventQueue().addEvent( new MemoryResponseEvent (
				Clock.getCurrentTime (), this, requesting_element, caches[cache_addr].data)) ;
		}
		else
		{
			read_value = 1;
			handleCacheMiss(addr, requesting_element);
		}
	}

	public void cacheWrite(int addr, int Data, Element requesting_element)
	{
		int cache_addr;
		
		if((int)(Math.log(size)/Math.log(2)) == 0)
		{
			cache_addr = 0;
		}
		else
		{
			cache_addr = addr << (32 - (int)(Math.log(size)/Math.log(2)));
			cache_addr = cache_addr >>> (32 - (int)(Math.log(size)/Math.log(2)));
		}
		
		if(caches[cache_addr].tag == addr)
		{
			caches[cache_addr].data = Data; 
			Simulator.getEventQueue().addEvent( new MemoryWriteEvent(
				Clock.getCurrentTime() + Configuration.mainMemoryLatency, this,
				containingProcessor.getMainMemory(), addr, Data));
			((MemoryAccess)requesting_element).EX_MA_Latch.setMA_busy(false);
			((MemoryAccess)requesting_element).MA_RW_Latch.setRW_enable(true);
		}
		else
		{
			read_value = 0;
			write_value = Data;
			handleCacheMiss(addr, requesting_element);
		}
	}
	
	public void handleCacheMiss(int addr, Element miss)
	{
		Simulator.getEventQueue().addEvent(new MemoryReadEvent(
				Clock.getCurrentTime() + Configuration.mainMemoryLatency, this,
				containingProcessor.getMainMemory(), addr));
		Miss_address = addr;
		Miss = miss;	
	}
	
	public void handleResponse(int data)
	{
		int Address;
		if((int)(Math.log(size)/Math.log(2)) == 0)
		{
			Address = 0;
		}
		else
		{
			Address = Miss_address << (32 - (int)(Math.log(size)/Math.log(2)));
			Address = Address >>> (32 - (int)(Math.log(size)/Math.log(2)));
		}	
	
		caches[Address].data = data;
		caches[Address].tag = Miss_address;
		
		if(read_value == 1)
		{
			Simulator.getEventQueue().addEvent( new MemoryResponseEvent(
				Clock.getCurrentTime(), this, Miss, data));
		}
		else
		{
			cacheWrite(Miss_address, write_value, Miss);
		}
	}

	@Override
	public void handleEvent(Event e)
	{
		if (e.getEventType() == EventType.MemoryRead)
		{
			MemoryReadEvent event = (MemoryReadEvent) e ;
			cacheRead(event.getAddressToReadFrom(), event.getRequestingElement());
		}
		else if(e.getEventType() == EventType.MemoryResponse )
		{
			MemoryResponseEvent event = (MemoryResponseEvent) e ;
			handleResponse(event.getValue());
		}
		else if(e.getEventType() == EventType.MemoryWrite)
		{
			MemoryWriteEvent event = (MemoryWriteEvent) e ;
			cacheWrite(event.getAddressToWriteTo(),event.getValue(), event.getRequestingElement());	
		}	
	}
}
