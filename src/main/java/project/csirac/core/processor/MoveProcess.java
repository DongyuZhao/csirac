package project.csirac.core.processor;

import project.emulator.framework.Bootstrap;
import project.emulator.framework.api.processor.ProcessUnit;
import project.emulator.framework.cpu.decoder.Command;
import project.emulator.framework.cpu.socket.IProcessor;

/**
 * Created by Dy.Zhao on 2016/1/23 0023.
 */
public class MoveProcess implements ProcessUnit
{

    protected IProcessor _processSocket;

    //private List<ProcessUnit> _postProcessorList = new ArrayList<>();

    public void  attachSocket(IProcessor _processorSocket)
    {
        this._processSocket = _processorSocket;
    }

    public MoveProcess()
    {
        //super(_processSocket);
    }

    // return if the pc register changed by instruction
    @Override
    public boolean process(Command command)
    {
        if (command != null)
        {
            this._processSocket.opCodeRegister().put(command.opCode);
            if (command.commandType.equals("S"))
            {
                if (command.opCode == Bootstrap.symbolTranslator.translateToCode("M"))
                {
                    int[] temp = this._processSocket.dataMemory().get(command.source / Bootstrap.innerConfig.cellPerUnit(), command.source % Bootstrap.innerConfig.cellPerUnit());
                    this._processSocket.register().put(command.target, temp);
                    return false;
                }
                else if (command.opCode == Bootstrap.symbolTranslator.translateToCode("A") || command.opCode == Bootstrap.symbolTranslator.translateToCode("B") || command.opCode == Bootstrap.symbolTranslator.translateToCode("C"))
                {
                    int[] temp = this._processSocket.register().get(command.source);
                    this._processSocket.register().put(command.target, temp);
                    return false;
                }
            }
            if (command.commandType.equals("D"))
            {
                if (command.opCode == Bootstrap.symbolTranslator.translateToCode("M"))
                {
                    int[] temp = this._processSocket.register().get(command.source);
                    this._processSocket.dataMemory().put(command.target / Bootstrap.innerConfig.cellPerUnit(), command.target % Bootstrap.innerConfig.cellPerUnit(), temp);
                    return false;
                }

                else if (command.opCode == Bootstrap.symbolTranslator.translateToCode("A") || command.opCode == Bootstrap.symbolTranslator.translateToCode("B") || command.opCode == Bootstrap.symbolTranslator.translateToCode("C"))
                {
                    int[] temp = this._processSocket.register().get(command.source);
                    this._processSocket.register().put(command.target, temp);
                    return false;
                }
            }
        }
        this._processSocket.pcRegister().put(-1);
        return true;
    }
}