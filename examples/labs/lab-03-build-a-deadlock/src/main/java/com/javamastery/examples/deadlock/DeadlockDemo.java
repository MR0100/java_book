package com.javamastery.examples.deadlock;

/**
 * THE HEADLINE DEADLOCK: a classic lock-ordering deadlock.
 *
 * <p>Two threads each grab two {@code synchronized} monitors, but in OPPOSITE
 * order:
 * <ul>
 *   <li>Thread-1 locks {@code lockA}, then tries to lock {@code lockB}.</li>
 *   <li>Thread-2 locks {@code lockB}, then tries to lock {@code lockA}.</li>
 * </ul>
 *
 * <p>If the interleaving lands so that each thread holds its first lock before
 * either reaches its second {@code synchronized} block, they wait on each other
 * forever. We force exactly that interleaving with a small {@code sleep} after
 * each thread grabs its first lock, so the deadlock is deterministic rather
 * than a rare race.
 *
 * <p>This {@code main} INTENTIONALLY HANGS. That is the point of the lab: run it,
 * capture a thread dump, and read the cycle. It is never invoked from tests.
 *
 * <p>How to observe (see README for full detail):
 * <pre>{@code
 *   mvn -q compile
 *   mvn -q exec:java ...   # or: java -cp target/classes com.javamastery.examples.deadlock.DeadlockDemo
 *   # in another terminal:
 *   jps                    # find the PID
 *   jstack <pid>           # or: jcmd <pid> Thread.print   (look for "Found 1 deadlock")
 *   # then Ctrl-C / kill the hung process
 * }</pre>
 */
public final class DeadlockDemo {

    /** First lock. We name it via a dedicated class so it shows up clearly in a thread dump. */
    static final class LockA { }

    /** Second lock. Distinct type so the monitor is easy to spot in the dump. */
    static final class LockB { }

    private final Object lockA = new LockA();
    private final Object lockB = new LockB();

    /** Acquires A then B. Sleeps in between to widen the deadlock window. */
    void workAB() {
        synchronized (lockA) {
            System.out.println(Thread.currentThread().getName() + ": holding lockA, want lockB");
            sleep(200); // force the interleaving: let the other thread grab lockB first
            synchronized (lockB) {
                System.out.println(Thread.currentThread().getName() + ": got BOTH locks (A then B)");
            }
        }
    }

    /** Acquires B then A — the OPPOSITE order. This inconsistency is the bug. */
    void workBA() {
        synchronized (lockB) {
            System.out.println(Thread.currentThread().getName() + ": holding lockB, want lockA");
            sleep(200); // force the interleaving: let the other thread grab lockA first
            synchronized (lockA) {
                System.out.println(Thread.currentThread().getName() + ": got BOTH locks (B then A)");
            }
        }
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        DeadlockDemo demo = new DeadlockDemo();

        Thread t1 = new Thread(demo::workAB, "deadlock-thread-AB");
        Thread t2 = new Thread(demo::workBA, "deadlock-thread-BA");

        t1.start();
        t2.start();

        System.out.println();
        System.out.println("Both threads started. This program is now DEADLOCKED and will hang.");
        System.out.println("PID = " + ProcessHandle.current().pid());
        System.out.println("Capture a thread dump now:  jstack " + ProcessHandle.current().pid());
        System.out.println("(or: jcmd " + ProcessHandle.current().pid() + " Thread.print). Then Ctrl-C to kill it.");
        System.out.println();

        // join() never returns: both worker threads are blocked forever.
        t1.join();
        t2.join();

        // Unreachable in practice. Present only so the structure is obvious.
        System.out.println("If you ever see this line, the deadlock did not happen.");
    }

    private DeadlockDemo() {
        // package-private constructor used by main; keep instances internal.
    }

    // Re-expose a public factory only for the JUnit deadlock-detection test,
    // which builds a SHORT-LIVED controlled scenario with a hard timeout.
    static DeadlockDemo newInstance() {
        return new DeadlockDemo();
    }
}
