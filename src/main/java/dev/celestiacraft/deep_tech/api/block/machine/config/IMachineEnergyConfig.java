package dev.celestiacraft.deep_tech.api.block.machine.config;

/**
 * 机器电力系统相关配置接口
 * <p>
 * 用于定义机器内部能量存储以及能量输入输出限制
 * <p>
 * 该接口中的方法没有默认实现, 每个机器都需要根据自身需求进行配置
 */
public interface IMachineEnergyConfig {
	/**
	 * 获取机器最大能量存储量
	 * <p>
	 * 该值表示机器内部能量容器能够存储的最大能量
	 *
	 * @return 机器最大能量容量
	 */
	int getMachineMaxEnergy();

	/**
	 * 获取机器最大能量接收速率
	 * <p>
	 * 该值表示机器每次传输周期内最多可以接收的能量数量
	 * <p>
	 * 当外部能量源向机器输入能量时, 输入速度不会超过该值
	 *
	 * @return 最大接收能量数量
	 */
	int getMaxReceive();

	/**
	 * 获取机器最大能量输出速率
	 * <p>
	 * 该值表示机器每次传输周期内最多可以向外输出的能量数量
	 * <p>
	 * 当其他设备从机器抽取能量时, 输出速度不会超过该值
	 *
	 * @return 最大输出能量数量
	 */
	int getMaxExtract();
}