/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.test;

import com.google.common.collect.ImmutableMap;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import org.jetbrains.kotlin.builtins.KotlinBuiltIns;
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl;
import org.jetbrains.kotlin.diagnostics.Diagnostic;
import org.jetbrains.kotlin.diagnostics.Errors;
import org.jetbrains.kotlin.diagnostics.Severity;
import org.jetbrains.kotlin.diagnostics.rendering.DefaultErrorMessages;
import org.jetbrains.kotlin.name.Name;
import org.jetbrains.kotlin.psi.KtExpression;
import org.jetbrains.kotlin.resolve.BindingContext;
import org.jetbrains.kotlin.resolve.BindingTrace;
import org.jetbrains.kotlin.resolve.diagnostics.Diagnostics;
import org.jetbrains.kotlin.storage.LockBasedStorageManager;
import org.jetbrains.kotlin.types.KotlinType;
import org.jetbrains.kotlin.types.expressions.KotlinTypeInfo;
import org.jetbrains.kotlin.util.slicedMap.ReadOnlySlice;
import org.jetbrains.kotlin.util.slicedMap.SlicedMap;
import org.jetbrains.kotlin.util.slicedMap.WritableSlice;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

public class KotlinTestUtils {

    @NotNull
    public static File getJdk9Home() {
        String jdk9 = System.getenv("JDK_9");
        if (jdk9 == null) {
            jdk9 = System.getenv("JDK_19");
            if (jdk9 == null) {
                throw new AssertionError("Environment variable JDK_9 is not set!");
            }
        }
        return new File(jdk9);
    }

    public static final BindingTrace DUMMY_TRACE = new BindingTrace() {
        @NotNull
        @Override
        public BindingContext getBindingContext() {
            return new BindingContext() {

                @NotNull
                @Override
                public Diagnostics getDiagnostics() {
                    return Diagnostics.Companion.getEMPTY();
                }

                @Override
                public <K, V> V get(ReadOnlySlice<K, V> slice, K key) {
                    return DUMMY_TRACE.get(slice, key);
                }

                @NotNull
                @Override
                public <K, V> Collection<K> getKeys(WritableSlice<K, V> slice) {
                    return DUMMY_TRACE.getKeys(slice);
                }

                @NotNull
                @TestOnly
                @Override
                public <K, V> ImmutableMap<K, V> getSliceContents(@NotNull ReadOnlySlice<K, V> slice) {
                    return ImmutableMap.of();
                }

                @Nullable
                @Override
                public KotlinType getType(@NotNull KtExpression expression) {
                    return DUMMY_TRACE.getType(expression);
                }

                @Override
                public void addOwnDataTo(@NotNull BindingTrace trace, boolean commitDiagnostics) {
                    // do nothing
                }
            };
        }

        @Override
        public <K, V> void record(WritableSlice<K, V> slice, K key, V value) {
        }

        @Override
        public <K> void record(WritableSlice<K, Boolean> slice, K key) {
        }

        @Override
        @SuppressWarnings("unchecked")
        public <K, V> V get(ReadOnlySlice<K, V> slice, K key) {
            if (slice == BindingContext.PROCESSED) return (V) Boolean.FALSE;
            return SlicedMap.DO_NOTHING.get(slice, key);
        }

        @NotNull
        @Override
        public <K, V> Collection<K> getKeys(WritableSlice<K, V> slice) {
            assert slice.isCollective();
            return Collections.emptySet();
        }

        @Nullable
        @Override
        public KotlinType getType(@NotNull KtExpression expression) {
            KotlinTypeInfo typeInfo = get(BindingContext.EXPRESSION_TYPE_INFO, expression);
            return typeInfo != null ? typeInfo.getType() : null;
        }

        @Override
        public void recordType(@NotNull KtExpression expression, @Nullable KotlinType type) {
        }

        @Override
        public void report(@NotNull Diagnostic diagnostic) {
            if (Errors.UNRESOLVED_REFERENCE_DIAGNOSTICS.contains(diagnostic.getFactory())) {
                throw new IllegalStateException("Unresolved: " + diagnostic.getPsiElement().getText());
            }
        }

        @Override
        public boolean wantsDiagnostics() {
            return false;
        }
    };

    public static BindingTrace DUMMY_EXCEPTION_ON_ERROR_TRACE = new BindingTrace() {
        @NotNull
        @Override
        public BindingContext getBindingContext() {
            return new BindingContext() {
                @NotNull
                @Override
                public Diagnostics getDiagnostics() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public <K, V> V get(ReadOnlySlice<K, V> slice, K key) {
                    return DUMMY_EXCEPTION_ON_ERROR_TRACE.get(slice, key);
                }

                @NotNull
                @Override
                public <K, V> Collection<K> getKeys(WritableSlice<K, V> slice) {
                    return DUMMY_EXCEPTION_ON_ERROR_TRACE.getKeys(slice);
                }

                @NotNull
                @TestOnly
                @Override
                public <K, V> ImmutableMap<K, V> getSliceContents(@NotNull ReadOnlySlice<K, V> slice) {
                    return ImmutableMap.of();
                }

                @Nullable
                @Override
                public KotlinType getType(@NotNull KtExpression expression) {
                    return DUMMY_EXCEPTION_ON_ERROR_TRACE.getType(expression);
                }

                @Override
                public void addOwnDataTo(@NotNull BindingTrace trace, boolean commitDiagnostics) {
                    // do nothing
                }
            };
        }

        @Override
        public <K, V> void record(WritableSlice<K, V> slice, K key, V value) {
        }

        @Override
        public <K> void record(WritableSlice<K, Boolean> slice, K key) {
        }

        @Override
        public <K, V> V get(ReadOnlySlice<K, V> slice, K key) {
            return null;
        }

        @NotNull
        @Override
        public <K, V> Collection<K> getKeys(WritableSlice<K, V> slice) {
            assert slice.isCollective();
            return Collections.emptySet();
        }

        @Nullable
        @Override
        public KotlinType getType(@NotNull KtExpression expression) {
            return null;
        }

        @Override
        public void recordType(@NotNull KtExpression expression, @Nullable KotlinType type) {
        }

        @Override
        public void report(@NotNull Diagnostic diagnostic) {
            if (diagnostic.getSeverity() == Severity.ERROR) {
                throw new IllegalStateException(DefaultErrorMessages.render(diagnostic));
            }
        }

        @Override
        public boolean wantsDiagnostics() {
            return true;
        }
    };

    // We suspect sequences of eight consecutive hexadecimal digits to be a package part hash code
    private static final Pattern STRIP_PACKAGE_PART_HASH_PATTERN = Pattern.compile("\\$([0-9a-f]{8})");

    private KotlinTestUtils() {
    }


    private static String homeDir = computeHomeDirectory();

    @NotNull
    public static String getHomeDirectory() {
        return homeDir;
    }

    @NotNull
    private static String computeHomeDirectory() {
        String userDir = System.getProperty("user.dir");
        File dir = new File(userDir == null ? "." : userDir);
        return FileUtil.toCanonicalPath(dir.getAbsolutePath());
    }






    /**
     * @return test data file name specified in the metadata of test method
     */
    @Nullable
    public static String getTestDataFileName(@NotNull Class<?> testCaseClass, @NotNull String testName) {
        try {
            Method method = testCaseClass.getDeclaredMethod(testName);
            return getMethodMetadata(method);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


    @Nullable
    private static String getMethodMetadata(Method method) {
        TestMetadata testMetadata = method.getAnnotation(TestMetadata.class);
        return (testMetadata != null) ? testMetadata.value() : null;
    }


    @NotNull
    public static ModuleDescriptorImpl createEmptyModule(@NotNull String name, @NotNull KotlinBuiltIns builtIns) {
        return new ModuleDescriptorImpl(Name.special(name), LockBasedStorageManager.NO_LOCKS, builtIns);
    }



    public static boolean isAllFilesPresentTest(String testName) {
        //noinspection SpellCheckingInspection
        return testName.toLowerCase().startsWith("allfilespresentin");
    }

}

