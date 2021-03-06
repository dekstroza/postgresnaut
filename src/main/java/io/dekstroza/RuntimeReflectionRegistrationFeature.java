package io.dekstroza;

import io.jaegertracing.internal.samplers.http.ProbabilisticSamplingStrategy;
import io.jaegertracing.internal.samplers.http.SamplingStrategyResponse;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeReflection;

public class RuntimeReflectionRegistrationFeature implements Feature {

    public void beforeAnalysis(BeforeAnalysisAccess access) {
        try {
            RuntimeReflection.register(true, ProbabilisticSamplingStrategy.class.getDeclaredField("samplingRate"));
            RuntimeReflection.register(true, SamplingStrategyResponse.class.getDeclaredField("probabilisticSampling"));
            RuntimeReflection.register(brave.SpanCustomizer.class);
            RuntimeReflection.register(brave.http.HttpClientAdapter.class);
            RuntimeReflection.register(brave.propagation.CurrentTraceContext.class);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
