import nidaqmx
from nidaqmx.constants import EncoderType, AngleUnits

def configure_encoder(task, counter, a_input_term, b_input_term, pulses_per_rev, initial_angle):

    # Add the angular encoder channel to the task
    task.ci_channels.add_ci_ang_encoder_chan(
        counter=counter,
        decoding_type=EncoderType.X_4,                                  # Use X4 decoding for higher resolution
        zidx_enable=False,                                              # Disable Z Index
        zidx_val=0.0,                                                   # Set Z Index reset value to 0
        zidx_phase=nidaqmx.constants.EncoderZIndexPhase.AHIGH_BHIGH,    # Define Z Index phase (not used here)
        units=AngleUnits.DEGREES,                                       # Set angle measurement in degrees
        pulses_per_rev=pulses_per_rev,                                  # Set pulses per revolution for the encoder
        initial_angle=initial_angle,                                    # Set initial angle for the encoder
    )
    # Configure the physical input terminals for channels A and B
    task.ci_channels.all.ci_encoder_a_input_term = a_input_term
    task.ci_channels.all.ci_encoder_b_input_term = b_input_term