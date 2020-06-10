class WyvernException {
	constructor(exn) {
		this.e = exn
	}
}

class TryResult {
	constructor(success, result) {
		this.success = success
		this.result = result
	}
}

exports.throwExn = function(exn) {
	throw new WyvernException(exn);
};

exports.tryFunc = function(f) {
	try {
		const result = f.apply();
		return new TryResult(true, result);
	} catch (error) {
		if (error instanceof WyvernException) {
			return new TryResult(false, error.e);
		} else {
			throw error;
		}
	}
};