package dev.celestiacraft.deep_tech.api.block;

/**
 * 这里是一些关于电力的配置
 * 这里的方法每个子类都要重写
 */
public interface IMachineEnergyConfig {
	/**
	 * 机器的最大能量存储量
	 *
	 * @return
	 */
	int getMachineMaxEnergy();

	/**
	 * 机器的最大接收能量
	 *
	 * @return
	 */
	int getMaxReceive();

	/**
	 * 机器的最大提取(输出)能量
	 *
	 * @return
	 */
	int getMaxExtract();
}