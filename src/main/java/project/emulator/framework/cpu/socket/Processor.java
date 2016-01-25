package project.emulator.framework.cpu.socket;

import project.emulator.framework.api.processor.ProcessUnit;
import project.emulator.framework.cpu.register.IOpCodeRegister;
import project.emulator.framework.cpu.register.IPcRegister;
import project.emulator.framework.cpu.register.IRegister;
import project.emulator.framework.cpu.decoder.Command;
import project.emulator.framework.cpu.decoder.IDecoder;
import project.emulator.framework.memory.IMemory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dy.Zhao on 2016/1/23 0023.
 */
public class Processor implements IProcessor
{

    private IDecoder _decoder;

    private IPcRegister _pcRegister;

    private IOpCodeRegister _opCodeRegister;

    private IRegister _register;

    private IMemory _instructionMemory;

    private IMemory _dataMemory;

    private List<ProcessUnit> _instructionMulticastList = new ArrayList<>();

    public Processor(IDecoder decoder, IPcRegister pcRegister, IOpCodeRegister opCodeRegister, IRegister register, IMemory instructionMemory, IMemory dataMemory, List<ProcessUnit> instructionMulticastList)
    {
        this._decoder = decoder;
        this._pcRegister = pcRegister;
        this._opCodeRegister = opCodeRegister;
        this._register = register;
        this._instructionMemory = instructionMemory;
        this._dataMemory = dataMemory;
        this._instructionMulticastList = instructionMulticastList;

        for (ProcessUnit processUnit: this._instructionMulticastList)
        {
            processUnit.attachSocket(this);
        }
    }

    @Override
    public void compute()
    {
        //int nextInstructionPointer;
        int instructionPointer = this._pcRegister.get();
        int[] instruction = this._instructionMemory.get(instructionPointer / this._instructionMemory.cellPerUnit(), instructionPointer % this._instructionMemory.cellPerUnit());
        //this._opCodeRegister.put(this._decoder.decode(instruction));
        Command[] decodedCommands = this.decode(instruction);
        boolean changeNextPointer = false;
        for (Command command : decodedCommands)
        {
            for (ProcessUnit processUnit : this._instructionMulticastList)
            {
                changeNextPointer = changeNextPointer || processUnit.process(command);
            }
        }
        if (!changeNextPointer)
        {
            this._pcRegister.put(instructionPointer + 1);
        }
        //return Bootstrap.innerConfig.finishSignal();
    }

    @Override
    public Command[] decode(int[] instruction)
    {
        if (this._decoder != null)
        {
            return this._decoder.decode(instruction);
        }
        return null;
    }

    @Override
    public void registerProcessorUnit(ProcessUnit processUnit)
    {
        if (processUnit != null && !this._instructionMulticastList.contains(processUnit))
        {
            processUnit.attachSocket(this);
            this._instructionMulticastList.add(processUnit);
        }
    }

    @Override
    public void registerDecoder(IDecoder decoder)
    {
        if (decoder != null)
        {
            this._decoder = decoder;
        }
    }



    public IDecoder decoder()
    {
        return this._decoder;
    }

    public IPcRegister pcRegister()
    {
        return this._pcRegister;
    }

    public IOpCodeRegister opCodeRegister()
    {
        return _opCodeRegister;
    }

    public IRegister register()
    {
        return _register;
    }

    public IMemory instructionMemory()
    {
        return _instructionMemory;
    }

    public IMemory dataMemory()
    {
        return _dataMemory;
    }

    public List<ProcessUnit> instructionMulticastList()
    {
        return _instructionMulticastList;
    }
}